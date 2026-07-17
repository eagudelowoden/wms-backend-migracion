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
    @Query(value = "EXEC pa_GetListClient :Id", nativeQuery = true)
    List<String> getListClient(@Param("Id") int usuarioId);

    @Query(value = "EXEC pa_GetIdClient :nombre", nativeQuery = true)
    Integer getIdClient(@Param("nombre") String nombre);

    @Query(value = "SELECT kitIngresoON FROM Cliente WHERE id = :id", nativeQuery = true)
    Boolean getKitIngresoON(@Param("id") int id);

    @Query(value = "SELECT id, nombre from Cliente where id in (92,151)", nativeQuery = true)
    List<Object[]> getClientes();

    @Query(value = "SELECT dbase FROM Cliente WHERE id = :id", nativeQuery = true)
    String getDbaseById(@Param("id") int id);

    @Transactional
    @Query(value = "DECLARE @NewId INT; EXEC pa_InsertCliente @Nombre = :nombre, @ColorCorporativo = :colorCorporativo, @Activo = :activo, @conn = :conn, @man_app = :manApp, @ImagenesEtiquetado = :imagenesEtiquetado, @ImagenesEmpaque = :imagenesEmpaque, @ImagenesIngreso = :imagenesIngreso, @PrnEtiquetado = :prnEtiquetado, @PrnEmpaque = :prnEmpaque, @PrnIngreso = :prnIngreso, @LblEtiquetado = :lblEtiquetado, @LblEmpaque = :lblEmpaque, @LblIngreso = :lblIngreso, @Archivos = :archivos, @Pallet = :pallet, @CodigoSap = :codigoSap, @ip_server = :ipServer, @dbase = :dbase, @db_user = :dbUser, @db_pass = :dbPass, @bandera = :bandera, @recogidaON = :recogidaON, @baseIngresoON = :baseIngresoON, @baseNoDisponibleON = :baseNoDisponibleON, @loteEmpaqueON = :loteEmpaqueON, @largoGuia = :largoGuia, @kitIngresoON = :kitIngresoON, @componenteON = :componenteON, @tipoOrigenUsuarioON = :tipoOrigenUsuarioON, @nivelClasificacionON = :nivelClasificacionON, @calidadON = :calidadON, @smartCardInfoON = :smartCardInfoON, @bloqueoReimpresionON = :bloqueoReimpresionON, @etiquetaUnitariaON = :etiquetaUnitariaON, @prealerta = :prealerta, @adicionPrealertaON = :adicionPrealertaON, @odooPqrsON = :odooPqrsON, @Id = @NewId OUTPUT; SELECT @NewId", nativeQuery = true)
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
                     @Param("odooPqrsON") Boolean odooPqrsON);

    @Transactional
    @Query(value = "DECLARE @Filas INT; EXEC pa_UpdateCliente @Id = :id, @Nombre = :nombre, @ColorCorporativo = :colorCorporativo, @Activo = :activo, @conn = :conn, @man_app = :manApp, @ImagenesEtiquetado = :imagenesEtiquetado, @ImagenesEmpaque = :imagenesEmpaque, @PrnEtiquetado = :prnEtiquetado, @PrnEmpaque = :prnEmpaque, @LblEtiquetado = :lblEtiquetado, @LblEmpaque = :lblEmpaque, @Archivos = :archivos, @Pallet = :pallet, @CodigoSap = :codigoSap, @ip_server = :ipServer, @dbase = :dbase, @db_user = :dbUser, @db_pass = :dbPass, @bandera = :bandera, @recogidaON = :recogidaON, @baseIngresoON = :baseIngresoON, @baseNoDisponibleON = :baseNoDisponibleON, @loteEmpaqueON = :loteEmpaqueON, @largoGuia = :largoGuia, @kitIngresoON = :kitIngresoON, @componenteON = :componenteON, @tipoOrigenUsuarioON = :tipoOrigenUsuarioON, @nivelClasificacionON = :nivelClasificacionON, @calidadON = :calidadON, @smartCardInfoON = :smartCardInfoON, @bloqueoReimpresionON = :bloqueoReimpresionON, @etiquetaUnitariaON = :etiquetaUnitariaON, @prealerta = :prealerta, @adicionPrealertaON = :adicionPrealertaON, @odooPqrsON = :odooPqrsON, @Filas = @Filas OUTPUT; SELECT @Filas", nativeQuery = true)
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
                     @Param("odooPqrsON") Boolean odooPqrsON);

    @Modifying
    @Transactional
    @Query(value = "EXEC pa_DeleteCliente @Id = :id", nativeQuery = true)
    void deleteSP(@Param("id") Integer id);

    @Query(value = "EXEC pa_GetClienteById @Id = :id", nativeQuery = true)
    List<Object[]> findByIdSP(@Param("id") Integer id);

    @Query(value = "EXEC pa_GetAllClientes @Termino = :termino", nativeQuery = true)
    List<Object[]> searchSP(@Param("termino") String termino);
}
