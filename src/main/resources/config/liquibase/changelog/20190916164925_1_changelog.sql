CREATE OR REPLACE FUNCTION public.table_names()
 RETURNS character varying[]
 LANGUAGE plpgsql
AS $function$
begin
	RETURN ARRAY_AGG(table_name::varchar)
    	from information_schema.tables
    	where table_schema = 'public'
        and table_name not in (
            'databasechangeloglock',
            'databasechangelog',
            'jhi_authority',
            'jhi_user_authority',
            'jhi_persistent_audit_event',
            'jhi_persistent_audit_evt_data',
            'spatial_ref_sys',
            'geography_columns',
            'geometry_columns',
            'raster_columns',
            'raster_overviews',
            'plv8_js_modules'
		);
end;
$function$
;

CREATE OR REPLACE FUNCTION public.cleanup()
	RETURNS void
 	LANGUAGE plpgsql
AS $function$
declare the_table varchar;
begin
    FOREACH the_table IN ARRAY table_names()
    LOOP
        execute format('DROP TRIGGER IF EXISTS public_%1$s_search_text_trigger ON %1$s', the_table);
        execute format('DROP FUNCTION IF EXISTS public_%1$s_search_text;', the_table);
        execute format('DROP FUNCTION IF EXISTS public_%1$s_text;', the_table);
    END LOOP;

    DROP FUNCTION IF EXISTS get_text;
    DROP FUNCTION IF EXISTS get_text_all;

end;
$function$;

SELECT cleanup();

DROP FUNCTION IF EXISTS cleanup;

CREATE OR REPLACE FUNCTION public.get_text(the_schema character varying, the_table character varying)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE searchable boolean;
DECLARE cols varchar := '';
DECLARE cols_fks varchar := '';
DECLARE new_cols varchar := '';
DECLARE new_cols_fks varchar := '';
DECLARE id_type varchar := 'bigint';
DECLARE exclusions varchar[];
BEGIN
	searchable := EXISTS (SELECT 1
		FROM information_schema.columns
		WHERE table_schema = the_schema AND table_name = the_table AND column_name = '_search');

	IF searchable THEN
		exclusions := ARRAY ['created_by','created_date','last_modified_by','last_modified_date','schedule'];

		cols := string_agg(format('COALESCE(%s, to_tsvector(''''))', format(case when data_type = 'jsonb' then 'to_tsvector(%s)' else 'to_tsvector(%s::text)' end, column_name)), ' || ')
	    	FROM information_schema.columns
	      	WHERE table_schema = the_schema
	        AND table_name = the_table
	        AND data_type NOT IN ('USER-DEFINED', 'timestamp without time zone', 'timestamp', 'boolean')
	        AND column_name != '_search'
	        AND column_name NOT IN (
	            SELECT kcu.column_name
	            FROM information_schema.table_constraints tco
	            JOIN information_schema.key_column_usage kcu
	            ON kcu.constraint_name = tco.constraint_name
	            AND kcu.constraint_schema = tco.constraint_schema
	            AND kcu.constraint_name = tco.constraint_name
	            WHERE tco.table_schema = the_schema
	            AND tco.table_name = the_table
	            AND tco.constraint_type IN ('PRIMARY KEY', 'FOREIGN KEY')
	           	UNION ALL
	           	SELECT column_name FROM UNNEST(exclusions) AS column_name
	    );

	   	cols_fks := concat_ws(' || ', cols, string_agg(format('COALESCE(%s, to_tsvector(''''))', format('%s_%s_text(%s, false)', ccu.table_schema, ccu.table_name, kcu.column_name)), ' || '))
	    	FROM information_schema.table_constraints tc
	        JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name
	        JOIN information_schema.constraint_column_usage ccu ON ccu.constraint_name = tc.constraint_name
	        WHERE constraint_type = 'FOREIGN KEY'
	        AND tc.table_name = the_table
	        AND tc.table_schema = the_schema;

	       raise notice '%', LENGTH(cols_fks);

		new_cols := string_agg(format('COALESCE(%s, to_tsvector(''''))', format(case when data_type = 'jsonb' then 'to_tsvector(NEW.%s)' else 'to_tsvector(NEW.%s::text)' end, column_name)), ' || ')
	       	FROM information_schema.columns
	       	WHERE table_schema = the_schema
	       	AND data_type NOT IN ('USER-DEFINED', 'timestamp without time zone', 'timestamp', 'boolean')
	        AND table_name = the_table
	        AND column_name != '_search'
	        AND column_name NOT IN (
	       		SELECT kcu.column_name
	            FROM information_schema.table_constraints tco
	            JOIN information_schema.key_column_usage kcu
	            ON kcu.constraint_name = tco.constraint_name
	            AND kcu.constraint_schema = tco.constraint_schema
	            AND kcu.constraint_name = tco.constraint_name
	            WHERE tco.table_schema = the_schema
	            AND tco.table_name = the_table
	            AND tco.table_schema = the_schema
	            AND tco.constraint_type IN ('PRIMARY KEY', 'FOREIGN KEY')
	            UNION ALL
	           	SELECT column_name FROM UNNEST(exclusions) AS column_name
	  	);

	    new_cols_fks := concat_ws(' || ', new_cols, string_agg(format('COALESCE(%s, to_tsvector(''''))', format('%s_%s_text(NEW.%s, false)', ccu.table_schema, ccu.table_name, kcu.column_name)), ' || '))
			FROM information_schema.table_constraints tc
	      	JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name
	      	JOIN information_schema.constraint_column_usage ccu ON ccu.constraint_name = tc.constraint_name
	      	WHERE constraint_type = 'FOREIGN KEY'
	       	AND tc.table_name = the_table
	       	AND tc.table_schema = the_schema;

		IF the_table = 'jhi_user' THEN
			id_type := 'varchar';
	   	END IF;

	   	EXECUTE format('DROP TRIGGER IF EXISTS %1$s_%2$s_search_text_trigger ON %1$s.%2$s;', the_schema, the_table);

	    IF LENGTH(cols) > 0 THEN
	    	EXECUTE format(
	        	'CREATE OR REPLACE FUNCTION %1$s_%2$s_text(IN entity_id %5$s, IN embed_fks BOOL, OUT _result tsvector) AS $T2$
	            BEGIN
	            	IF embed_fks THEN
	            		SELECT %3$s into _result FROM %1$s.%2$s WHERE id = entity_id;
	            	ELSE
	            		SELECT %4$s into _result FROM %1$s.%2$s WHERE id = entity_id;
	            	END IF;
	            	RETURN;
	            END
	            $T2$ LANGUAGE plpgsql;', the_schema, the_table, cols_fks, cols, id_type);
	  	ELSEIF LENGTH(cols_fks) > 0 THEN
	       	EXECUTE format(
	            'CREATE OR REPLACE FUNCTION %1$s_%2$s_text(IN entity_id %4$s, IN embed_fks BOOL, OUT _result tsvector) AS $T2$
	            BEGIN
	            	IF embed_fks THEN
	            		SELECT %3$s into _result FROM %1$s.%2$s WHERE id = entity_id;
	            	END IF;
	            	RETURN;
	            END
	            $T2$ LANGUAGE plpgsql;', the_schema, the_table, cols_fks, id_type);
	    ELSE
	    	EXECUTE format(
	            'CREATE OR REPLACE FUNCTION %1$s_%2$s_text(IN entity_id %3$s, IN embed_fks BOOL, OUT _result tsvector) AS $T2$
	            BEGIN
	            	_result := tsvector('''');
	            	RETURN;
	            END
	            $T2$ LANGUAGE plpgsql;', the_schema, the_table, id_type);
	   	END IF;

	    IF(LENGTH(new_cols_fks) > 0) THEN
		  	EXECUTE format('CREATE or replace FUNCTION %1$s_%2$s_search_text()
			            RETURNS trigger AS $T2$
			            BEGIN
			            IF TG_OP = ''UPDATE'' AND NEW._search != OLD._search THEN
			            	RETURN NEW;
						END IF;
						SELECT %3$s into NEW._search;
			            RETURN NEW;
			            END
			            $T2$ LANGUAGE plpgsql', the_schema, the_table, new_cols_fks);

		   	EXECUTE format('CREATE TRIGGER %1$s_%2$s_search_text_trigger BEFORE INSERT or UPDATE on %1$s.%2$s
		            		FOR EACH ROW EXECUTE PROCEDURE %1$s_%2$s_search_text();', the_schema, the_table);
		END IF;

		EXECUTE format('CREATE INDEX IF NOT EXISTS %1$s_%2$s_search_idx ON %1$s.%2$s USING gist (_search)', the_schema, the_table);
	END IF;
END
$function$
;

CREATE OR REPLACE FUNCTION public.get_text_all()
 RETURNS void
 LANGUAGE plpgsql
AS $function$
declare table_names varchar[];
declare the_table varchar;
begin
    FOREACH the_table IN ARRAY table_names()
    LOOP
        EXECUTE format('ALTER TABLE %s ADD COLUMN if not exists _search tsvector;', the_table);
        PERFORM get_text('public',  the_table);
    END LOOP;
end;
$function$
;

select get_text_all();

CREATE OR REPLACE FUNCTION public.on_alter_table_func()
 RETURNS event_trigger
 LANGUAGE plpgsql
AS $function$
DECLARE r RECORD;
DECLARE t VARCHAR;
DECLARE s VARCHAR;
BEGIN
	r := pg_event_trigger_ddl_commands();
	t := substring(r.object_identity, position('.' in r.object_identity) + 1);
	s := substring(r.object_identity, 0, position('.' in r.object_identity));
	IF t = ANY(table_names()) THEN
		PERFORM get_text(s, t);
	END IF;
END
$function$
;
