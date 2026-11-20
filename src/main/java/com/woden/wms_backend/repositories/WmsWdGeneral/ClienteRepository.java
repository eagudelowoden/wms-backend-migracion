package com.woden.wms_backend.repositories.WmsWdGeneral;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.woden.wms_backend.models.WmsWdGeneral.ClienteModel;
import com.woden.wms_backend.repositories.BaseRepository;

@Repository
@Primary
public interface ClienteRepository extends BaseRepository<ClienteModel, Integer> {
    @Query(value = "SELECT f.nombre FROM general.pa_getlistclient(:Id) f", nativeQuery = true)
    List<String> getListClient(@Param("Id") int usuarioId);

    @Query(value = "SELECT CAST(f.id AS integer) FROM general.pa_getidclient(:nombre) f", nativeQuery = true)
    Integer getIdClient(@Param("nombre") String nombre);

    @Query(value = "SELECT kitIngresoON FROM Cliente WHERE id = :id", nativeQuery = true)
    Boolean getKitIngresoON(@Param("id") int id);

    @Query(value = "SELECT id, nombre from Cliente where id in (92,151)", nativeQuery = true)
    List<Object[]> getClientes();

    @Query(value = "SELECT dbase FROM Cliente WHERE id = :id", nativeQuery = true)
    String getDbaseById(@Param("id") int id);

    @Query(value = "SELECT COALESCE(SUM(cnt), 0) FROM (SELECT COUNT(*) cnt FROM UsuarioCliente WHERE clienteId = :id UNION ALL SELECT COUNT(*) FROM ClienteParametro WHERE clienteId = :id) refs", nativeQuery = true)
    Integer countMovements(@Param("id") Integer id);

    @Transactional
    @Query(value = "SELECT general.pa_insertcliente(p_nombre => CAST(:nombre AS varchar), p_colorcorporativo => CAST(:colorCorporativo AS varchar), p_activo => CAST(:activo AS integer), p_conn => CAST(:conn AS varchar), p_man_app => CAST(:manApp AS integer), p_imagenesetiquetado => CAST(:imagenesEtiquetado AS varchar), p_imagenesempaque => CAST(:imagenesEmpaque AS varchar), p_imagenesingreso => CAST(:imagenesIngreso AS varchar), p_prnetiquetado => CAST(:prnEtiquetado AS varchar), p_prnempaque => CAST(:prnEmpaque AS varchar), p_prningreso => CAST(:prnIngreso AS varchar), p_lbletiquetado => CAST(:lblEtiquetado AS varchar), p_lblempaque => CAST(:lblEmpaque AS varchar), p_lblingreso => CAST(:lblIngreso AS varchar), p_archivos => CAST(:archivos AS varchar), p_pallet => CAST(:pallet AS varchar), p_codigosap => CAST(:codigoSap AS varchar), p_ip_server => CAST(:ipServer AS varchar), p_dbase => CAST(:dbase AS varchar), p_db_user => CAST(:dbUser AS varchar), p_db_pass => CAST(:dbPass AS varchar), p_bandera => CAST(:bandera AS varchar), p_recogidaon => CAST(:recogidaON AS integer), p_baseingresoon => CAST(:baseIngresoON AS integer), p_basenodisponibleon => CAST(:baseNoDisponibleON AS integer), p_loteempaqueon => CAST(:loteEmpaqueON AS integer), p_largoguia => CAST(:largoGuia AS varchar), p_kitingresoon => CAST(:kitIngresoON AS integer), p_componenteon => CAST(:componenteON AS integer), p_tipoorigenusuarioon => CAST(:tipoOrigenUsuarioON AS integer), p_nivelclasificacionon => CAST(:nivelClasificacionON AS integer), p_calidadon => CAST(:calidadON AS integer), p_smartcardinfoon => CAST(:smartCardInfoON AS integer), p_bloqueoreimpresionon => CAST(:bloqueoReimpresionON AS integer), p_etiquetaunitariaon => CAST(:etiquetaUnitariaON AS integer), p_prealerta => CAST(:prealerta AS varchar), p_adicionprealertaon => CAST(:adicionPrealertaON AS integer), p_odoopqrson => CAST(:odooPqrsON AS boolean))", nativeQuery = true)
    Integer insertSP(@Param("nombre") String nombre,
                     @Param("colorCorporativo") String colorCorporativo,
                     @Param("activo") Integer activo,
                     @Param("conn") String conn,
                     @Param("manApp") Integer manApp,
                     @Param("imagenesEtiquetado") String imagenesEtiquetado,
                     @Param("imagenesEmpaque") String imagenesEmpaque,
                     @Param("imagenesIngreso") String imagenesIngreso,
                     @Param("prnEtiquetado") String prnEtiquetado,
                     @Param("prnEmpaque") String prnEmpaque,
                     @Param("prnIngreso") String prnIngreso,
                     @Param("lblEtiquetado") String lblEtiquetado,
                     @Param("lblEmpaque") String lblEmpaque,
                     @Param("lblIngreso") String lblIngreso,
                     @Param("archivos") String archivos,
                     @Param("pallet") String pallet,
                     @Param("codigoSap") String codigoSap,
                     @Param("ipServer") String ipServer,
                     @Param("dbase") String dbase,
                     @Param("dbUser") String dbUser,
                     @Param("dbPass") String dbPass,
                     @Param("bandera") String bandera,
                     @Param("recogidaON") Integer recogidaON,
                     @Param("baseIngresoON") Integer baseIngresoON,
                     @Param("baseNoDisponibleON") Integer baseNoDisponibleON,
                     @Param("loteEmpaqueON") Integer loteEmpaqueON,
                     @Param("largoGuia") String largoGuia,
                     @Param("kitIngresoON") Integer kitIngresoON,
                     @Param("componenteON") Integer componenteON,
                     @Param("tipoOrigenUsuarioON") Integer tipoOrigenUsuarioON,
                     @Param("nivelClasificacionON") Integer nivelClasificacionON,
                     @Param("calidadON") Integer calidadON,
                     @Param("smartCardInfoON") Integer smartCardInfoON,
                     @Param("bloqueoReimpresionON") Integer bloqueoReimpresionON,
                     @Param("etiquetaUnitariaON") Integer etiquetaUnitariaON,
                     @Param("prealerta") String prealerta,
                     @Param("adicionPrealertaON") Integer adicionPrealertaON,
                     @Param("odooPqrsON") Boolean odooPqrsON,
                     @Param("serialMasterCalidadON") Integer serialMasterCalidadON);

    @Transactional
    @Query(value = "SELECT general.pa_updatecliente(p_id => CAST(:id AS integer), p_nombre => CAST(:nombre AS varchar), p_colorcorporativo => CAST(:colorCorporativo AS varchar), p_activo => CAST(:activo AS integer), p_conn => CAST(:conn AS varchar), p_man_app => CAST(:manApp AS integer), p_imagenesetiquetado => CAST(:imagenesEtiquetado AS varchar), p_imagenesempaque => CAST(:imagenesEmpaque AS varchar), p_prnetiquetado => CAST(:prnEtiquetado AS varchar), p_prnempaque => CAST(:prnEmpaque AS varchar), p_lbletiquetado => CAST(:lblEtiquetado AS varchar), p_lblempaque => CAST(:lblEmpaque AS varchar), p_archivos => CAST(:archivos AS varchar), p_pallet => CAST(:pallet AS varchar), p_codigosap => CAST(:codigoSap AS varchar), p_ip_server => CAST(:ipServer AS varchar), p_dbase => CAST(:dbase AS varchar), p_db_user => CAST(:dbUser AS varchar), p_db_pass => CAST(:dbPass AS varchar), p_bandera => CAST(:bandera AS varchar), p_recogidaon => CAST(:recogidaON AS integer), p_baseingresoon => CAST(:baseIngresoON AS integer), p_basenodisponibleon => CAST(:baseNoDisponibleON AS integer), p_loteempaqueon => CAST(:loteEmpaqueON AS integer), p_largoguia => CAST(:largoGuia AS varchar), p_kitingresoon => CAST(:kitIngresoON AS integer), p_componenteon => CAST(:componenteON AS integer), p_tipoorigenusuarioon => CAST(:tipoOrigenUsuarioON AS integer), p_nivelclasificacionon => CAST(:nivelClasificacionON AS integer), p_calidadon => CAST(:calidadON AS integer), p_smartcardinfoon => CAST(:smartCardInfoON AS integer), p_bloqueoreimpresionon => CAST(:bloqueoReimpresionON AS integer), p_etiquetaunitariaon => CAST(:etiquetaUnitariaON AS integer), p_prealerta => CAST(:prealerta AS varchar), p_adicionprealertaon => CAST(:adicionPrealertaON AS integer), p_odoopqrson => CAST(:odooPqrsON AS boolean))", nativeQuery = true)
    Integer updateSP(@Param("id") Integer id,
                     @Param("nombre") String nombre,
                     @Param("colorCorporativo") String colorCorporativo,
                     @Param("activo") Integer activo,
                     @Param("conn") String conn,
                     @Param("manApp") Integer manApp,
                     @Param("imagenesEtiquetado") String imagenesEtiquetado,
                     @Param("imagenesEmpaque") String imagenesEmpaque,
                     @Param("prnEtiquetado") String prnEtiquetado,
                     @Param("prnEmpaque") String prnEmpaque,
                     @Param("lblEtiquetado") String lblEtiquetado,
                     @Param("lblEmpaque") String lblEmpaque,
                     @Param("archivos") String archivos,
                     @Param("pallet") String pallet,
                     @Param("codigoSap") String codigoSap,
                     @Param("ipServer") String ipServer,
                     @Param("dbase") String dbase,
                     @Param("dbUser") String dbUser,
                     @Param("dbPass") String dbPass,
                     @Param("bandera") String bandera,
                     @Param("recogidaON") Integer recogidaON,
                     @Param("baseIngresoON") Integer baseIngresoON,
                     @Param("baseNoDisponibleON") Integer baseNoDisponibleON,
                     @Param("loteEmpaqueON") Integer loteEmpaqueON,
                     @Param("largoGuia") String largoGuia,
                     @Param("kitIngresoON") Integer kitIngresoON,
                     @Param("componenteON") Integer componenteON,
                     @Param("tipoOrigenUsuarioON") Integer tipoOrigenUsuarioON,
                     @Param("nivelClasificacionON") Integer nivelClasificacionON,
                     @Param("calidadON") Integer calidadON,
                     @Param("smartCardInfoON") Integer smartCardInfoON,
                     @Param("bloqueoReimpresionON") Integer bloqueoReimpresionON,
                     @Param("etiquetaUnitariaON") Integer etiquetaUnitariaON,
                     @Param("prealerta") String prealerta,
                     @Param("adicionPrealertaON") Integer adicionPrealertaON,
                     @Param("odooPqrsON") Boolean odooPqrsON,
                     @Param("serialMasterCalidadON") Integer serialMasterCalidadON);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Cliente SET serialMasterCalidadON = (CAST(:valor AS integer) <> 0) WHERE Id = :id", nativeQuery = true)
    void setSerialMasterCalidadON(@Param("id") Integer id, @Param("valor") Integer valor);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Cliente SET Activo = (CAST(:estado AS integer) <> 0) WHERE Id = :id", nativeQuery = true)
    void toggle(@Param("id") Integer id, @Param("estado") Integer estado);

    @Transactional
    @Query(value = "SELECT 1 FROM general.pa_deletecliente(:id)", nativeQuery = true)
    void deleteSP(@Param("id") Integer id);

    @Query(value = "SELECT CAST(f.\"Id\" AS integer), f.\"Nombre\", f.\"ColorCorporativo\", CAST(f.\"Activo\" AS boolean), f.conn, CAST(f.man_app AS integer), f.\"ImagenesEtiquetado\", f.\"ImagenesEmpaque\", f.\"ImagenesIngreso\", f.\"PrnEtiquetado\", f.\"PrnEmpaque\", f.\"PrnIngreso\", f.\"LblEtiquetado\", f.\"LblEmpaque\", f.\"LblIngreso\", f.\"Archivos\", f.\"Pallet\", f.\"CodigoSap\", f.ip_server, f.dbase, f.db_user, f.db_pass, f.bandera, CAST(f.\"recogidaON\" AS boolean), CAST(f.\"baseIngresoON\" AS boolean), CAST(f.\"baseNoDisponibleON\" AS boolean), CAST(f.\"loteEmpaqueON\" AS boolean), f.\"largoGuia\", CAST(f.\"kitIngresoON\" AS boolean), CAST(f.\"componenteON\" AS boolean), CAST(f.\"tipoOrigenUsuarioON\" AS boolean), CAST(f.\"nivelClasificacionON\" AS boolean), CAST(f.\"calidadON\" AS integer), CAST(f.\"smartCardInfoON\" AS boolean), CAST(f.\"bloqueoReimpresionON\" AS integer), CAST(f.\"etiquetaUnitariaON\" AS boolean), f.prealerta, CAST(f.\"adicionPrealertaON\" AS boolean), CAST(f.\"odooPqrsON\" AS boolean), (SELECT CAST(c.serialmastercalidadon AS integer) FROM general.cliente c WHERE c.id = :id) FROM general.pa_getclientebyid(:id) f", nativeQuery = true)
    List<Object[]> findByIdSP(@Param("id") Integer id);

    @Query(value = "SELECT CAST(f.\"Id\" AS integer), f.\"Nombre\", f.\"ColorCorporativo\", CAST(f.\"Activo\" AS boolean), f.dbase, f.ip_server, f.bandera FROM general.pa_getallclientes(CAST(:termino AS varchar)) f", nativeQuery = true)
    List<Object[]> searchSP(@Param("termino") String termino);
}
