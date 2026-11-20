-- Parches a FuncionesPostgress.sql (esquema general)
-- El conversor automatico dejo SQL dinamico roto en estas funciones:
--   * 'FROM' || v_db / 'JOIN' || v_db  -> falta el espacio ("FROMwmswdclaro.menu")
--   * VALUES (@m, @p)                  -> placeholders de SQL Server, invalidos en PostgreSQL
-- Las funciones de abajo son las que usa hoy el backend desde los repositorios de general.
-- Ejecutar una sola vez en la base WmsWoden.

-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION general.pa_getlistusuariotipoorigen(p_usuarioid integer, p_clienteid integer)
 RETURNS TABLE("Id" text, "Codigo" text)
 LANGUAGE plpgsql
 SET search_path TO 'general', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_usuarioclienteid integer;
    v_basedatos varchar(128);
    v_sql text;
BEGIN
    SELECT id INTO v_usuarioclienteid
    FROM usuariocliente
    WHERE usuarioid = p_usuarioid
    AND clienteid = p_clienteid;
    SELECT dbase INTO v_basedatos
    FROM cliente
    WHERE id = p_clienteid;
    v_sql := '    SELECT m.id, m.codigo
    FROM usuariotipoorigen uto
    INNER JOIN ' || quote_ident(lower((v_basedatos)::text)) || '.maestro m ON uto.tipoorigenid = m.id
    INNER JOIN ' || quote_ident(lower((v_basedatos)::text)) || '.tipomaestro tm ON m.tipomaestroid = tm.id
    WHERE uto.usuarioclienteid = $1
    AND tm.nombre = ''Tipo Origenes''
    ORDER BY m.codigo';
    RETURN QUERY EXECUTE 'SELECT q.c1::text, q.c2::text FROM (' || rtrim(v_sql, E'; \n\t\r') || ') AS q(c1, c2)' USING v_usuarioclienteid;
END;
$function$;

-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION general.pa_getavailablegruposforprofile(p_perfilid integer, p_cliente character varying)
 RETURNS TABLE(id text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'general', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_db varchar(200);
    v_sql text;
BEGIN
    v_db := quote_ident(lower((p_cliente)::text)) || '.';
    v_sql := '        SELECT m.id, m.descripcion FROM ' || v_db || 'menu m
        WHERE m.id_padre = ''0''
        AND m.id NOT IN (
            SELECT mp.idmenu FROM ' || v_db || 'menu_perfil mp WHERE mp.idperfil = $1
        )
        ORDER BY m.orden';
    RETURN QUERY EXECUTE 'SELECT q.c1::text, q.c2::text FROM (' || rtrim(v_sql, E'; \n\t\r') || ') AS q(c1, c2)' USING p_perfilid;
END;
$function$;

-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION general.pa_getassignedgruposforprofile(p_perfilid integer, p_cliente character varying)
 RETURNS TABLE(id text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'general', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_db varchar(200);
    v_sql text;
BEGIN
    v_db := quote_ident(lower((p_cliente)::text)) || '.';
    v_sql := '        SELECT m.id, m.descripcion FROM ' || v_db || 'menu m
        INNER JOIN ' || v_db || 'menu_perfil mp ON m.id = mp.idmenu AND mp.idperfil = $1
        WHERE m.id_padre = ''0''
        ORDER BY m.orden';
    RETURN QUERY EXECUTE 'SELECT q.c1::text, q.c2::text FROM (' || rtrim(v_sql, E'; \n\t\r') || ') AS q(c1, c2)' USING p_perfilid;
END;
$function$;

-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION general.pa_getavailableitemsforprofile(p_perfilid integer, p_idpadre character varying, p_cliente character varying)
 RETURNS TABLE(id text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'general', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_db varchar(200);
    v_sql text;
BEGIN
    v_db := quote_ident(lower((p_cliente)::text)) || '.';
    v_sql := '        SELECT m.id, m.descripcion FROM ' || v_db || 'menu m
        WHERE m.id_padre = $2
        AND m.id NOT IN (
            SELECT mp.idmenu FROM ' || v_db || 'menu_perfil mp WHERE mp.idperfil = $1
        )
        ORDER BY m.orden';
    RETURN QUERY EXECUTE 'SELECT q.c1::text, q.c2::text FROM (' || rtrim(v_sql, E'; \n\t\r') || ') AS q(c1, c2)' USING p_perfilid, p_idpadre;
END;
$function$;

-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION general.pa_getassigneditemsforprofile(p_perfilid integer, p_idpadre character varying, p_cliente character varying)
 RETURNS TABLE(id text, descripcion text)
 LANGUAGE plpgsql
 SET search_path TO 'general', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_db varchar(200);
    v_sql text;
BEGIN
    v_db := quote_ident(lower((p_cliente)::text)) || '.';
    v_sql := '        SELECT m.id, m.descripcion FROM ' || v_db || 'menu m
        INNER JOIN ' || v_db || 'menu_perfil mp ON m.id = mp.idmenu AND mp.idperfil = $1
        WHERE m.id_padre = $2
        ORDER BY m.orden';
    RETURN QUERY EXECUTE 'SELECT q.c1::text, q.c2::text FROM (' || rtrim(v_sql, E'; \n\t\r') || ') AS q(c1, c2)' USING p_perfilid, p_idpadre;
END;
$function$;

-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION general.pa_insertmenuperfil(p_idmenu character varying, p_idperfil integer, p_cliente character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'general', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_db varchar(200);
    v_sql text;
BEGIN
    v_db := quote_ident(lower((p_cliente)::text)) || '.';
    v_sql := 'INSERT INTO ' || v_db || 'menu_perfil (idmenu, idperfil) VALUES ($1, $2)';
    EXECUTE v_sql USING p_idmenu, p_idperfil;
END;
$function$;

-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION general.pa_syncmenuperfil(p_perfilid integer, p_menuids text, p_cliente character varying)
 RETURNS void
 LANGUAGE plpgsql
 SET search_path TO 'general', 'mssql', 'public'
AS $function$
#variable_conflict use_column
DECLARE
    v_db varchar(200);
    v_item text;
BEGIN
    v_db := quote_ident(lower((p_cliente)::text)) || '.';
    EXECUTE 'DELETE FROM ' || v_db || 'menu_perfil WHERE idperfil = $1' USING p_perfilid;
    FOR v_item IN SELECT trim(x) FROM unnest(string_to_array(p_menuids, ',')) AS x LOOP
        IF length(v_item) > 0 THEN
            EXECUTE 'INSERT INTO ' || v_db || 'menu_perfil (idmenu, idperfil) VALUES ($1, $2)' USING v_item, p_perfilid;
        END IF;
    END LOOP;
END;
$function$;
