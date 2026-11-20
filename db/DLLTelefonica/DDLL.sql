create database WmsWdTelefonicaColombiaBOG
go

use WmsWdTelefonicaColombiaBOG
go

grant connect on database :: WmsWdTelefonicaColombiaBOG to adminmssql
go

deny backup database, backup log on database :: WmsWdTelefonicaColombiaBOG to adminmssql
go

grant connect on database :: WmsWdTelefonicaColombiaBOG to dbo
go

grant connect on database :: WmsWdTelefonicaColombiaBOG to onlyreadmssql
go

grant view any column encryption key definition, view any column master key definition on database :: WmsWdTelefonicaColombiaBOG to [public]
go

grant connect on database :: WmsWdTelefonicaColombiaBOG to readermssql
go

deny backup database, backup log on database :: WmsWdTelefonicaColombiaBOG to readermssql
go

create table dbo.BaseDespacho
(
    Id        int identity
        constraint PK_BaseDespacho
            primary key,
    Serial    varchar(255),
    CodigoSap varchar(255),
    EstadoSap varchar(255),
    EstadoRR  varchar(255),
    Lote      varchar(255)
)
go

create table dbo.BaseEmpaque
(
    Id        int identity
        constraint PK_Iq09Empaque
            primary key,
    Serial    varchar(255),
    CodigoSap varchar(255),
    EstadoSap varchar(255),
    EstadoRR  varchar(255),
    Lote      varchar(255)
)
go

create index Serial_IDX
    on dbo.BaseEmpaque (Serial)
go

create table dbo.BaseEtiquetado
(
    Id        int identity
        constraint PK_BaseEtiquetado
            primary key,
    Serial    varchar(255),
    CodigoSap varchar(255),
    EstadoSap varchar(255),
    EstadoRR  varchar(255)
)
go

create table dbo.BaseIngreso
(
    Id        int identity
        constraint PK_BaseIngreso
            primary key,
    Serial    varchar(255),
    CodigoSap varchar(255),
    EstadoSap varchar(255),
    EstadoRR  varchar(255),
    Lote      varchar(255)
)
go

create table dbo.BaseInventario
(
    Id        int identity
        constraint PK_BaseInventario
            primary key,
    Serial    varchar(255),
    CodigoSap varchar(255),
    EstadoSap varchar(255),
    EstadoRR  varchar(255)
)
go

create table dbo.BaseSeparacion
(
    Id        int identity
        constraint PK_CargueSAP
            primary key,
    Serial    varchar(255),
    CodigoSap varchar(255),
    EstadoSap varchar(255),
    EstadoRR  varchar(255)
)
go

create table dbo.BaseXLog
(
    Id          int identity
        constraint PK_BaseXLog
            primary key,
    Fecha       date      not null,
    Hora        time      not null,
    Base        nchar(10) not null,
    NumRegistro int       not null
)
go

create table dbo.Cargo
(
    Id     int not null
        primary key,
    nombre varchar(255)
)
go

create table dbo.Ciclico
(
    Id        int identity
        constraint PK_Ciclico
            primary key,
    Nombre    varchar(50) not null,
    UsuarioId int         not null,
    Estado    varchar(20) not null
)
go

create table dbo.CiclicoSerial
(
    Id        int identity
        constraint PK_CiclicoSerial
            primary key,
    CiclicoId int not null
        constraint FK_CiclicoSerial_Ciclico
            references dbo.Ciclico,
    SerialId  int not null,
    Estado    varchar(255),
    Novedad   varchar(255),
    UsuarioId int not null,
    Fecha     varchar(255)
)
go

create table dbo.Ciudad
(
    id           int identity
        constraint PK_Ciudad
            primary key,
    nombre       varchar(120),
    codigoCiudad varchar(20),
    activo       bit
)
go

create table dbo.Clasificacion
(
    Id                int identity
        constraint PK_Clasificacion
            primary key,
    serialId          int         not null,
    serial            varchar(50) not null,
    mac               varchar(50) not null,
    serial3           varchar(30),
    serial4           varchar(30),
    serial5           varchar(30),
    codigoSapId       int         not null,
    nivelId           int         not null,
    loteId            int,
    estadoEnviado     varchar(30),
    usuarioId         int         not null,
    fecha             datetime    not null,
    UsuarioAsignadoId int,
    FechaCierre       datetime
)
go

create index [<ClasificacionTel03>]
    on dbo.Clasificacion (fecha) include (codigoSapId)
go

create index [<ClasificacionTel004>]
    on dbo.Clasificacion (codigoSapId, fecha)
go

create table dbo.Clinico
(
    Id        int not null
        primary key,
    estado    varchar(255),
    nombre    varchar(255),
    usuarioId int not null
)
go

create table dbo.CodigoSapAccesorio
(
    id          int identity
        primary key,
    codigosapId int,
    AccesorioId int
)
go

create table dbo.Contrato
(
    codigo             varchar(20)  not null
        primary key,
    descripcion        varchar(200) not null,
    fecha_inicial      datetime     not null,
    fecha_final        datetime     not null,
    activo             bit          not null,
    fecha_creacion     datetime     not null,
    fecha_modificacion datetime,
    usuario_creo       int          not null,
    usuario_modifico   int
)
go

create table dbo.Correo
(
    Id             int not null
        primary key,
    asunto         varchar(255),
    clienteId      int not null,
    destinatarioId int not null,
    estado         varchar(255),
    fecha          varchar(255),
    mensaje        varchar(255),
    pedidoId       int not null,
    remitente      varchar(255),
    remitenteId    int not null,
    tipo           varchar(255)
)
go

create table dbo.Cosmetica
(
    Id          int identity
        constraint PK_Cosmetica
            primary key,
    serialId    int         not null,
    serial      varchar(50) not null,
    mac         varchar(50) not null,
    serial3     varchar(30),
    serial4     varchar(30),
    serial5     varchar(30),
    codigoSapId int         not null,
    palletId    int         not null,
    nivelId     int         not null,
    loteId      int,
    usuarioId   int         not null,
    fecha       datetime    not null,
    FechaCierre datetime
)
go

create index [<Cosmetica0054>]
    on dbo.Cosmetica (fecha) include (codigoSapId)
go

create index [<CosmeticaTel005>]
    on dbo.Cosmetica (codigoSapId, fecha)
go

create table dbo.DbaVersion
(
    id          int identity
        constraint PK_DbaVersion
            primary key,
    version     int,
    description varchar(120),
    insert_date datetime,
    user_name   varchar(50)
)
go

create table dbo.DespachoAccesorio
(
    Id                int identity,
    CodigoSapId       int         not null,
    TipoAccesorio     varchar(20) not null,
    TipoOrigenId      int         not null,
    OrigenId          int         not null,
    PalletId          int         not null,
    EstadoId          int         not null,
    EstadoLimpiezaId  int,
    Documento         varchar(50),
    Observacion       nvarchar(250),
    Guia              varchar(50),
    UsuarioId         int         not null,
    FechaIngreso      datetime    not null,
    SerialEmpaque     varchar(50),
    Caja              int,
    Fecha             datetime,
    PedidoSap         varchar(20),
    AccesoryId        int,
    fechaLimpieza     datetime,
    fechaEmpaque      datetime,
    UsuarioLimpiezaId int
)
go

create index IX_RC_DespAcc_PalletId
    on dbo.DespachoAccesorio (PalletId)
go

create index IX_RC_DespAcc_CodSap
    on dbo.DespachoAccesorio (CodigoSapId)
go

create index IX_RC_DespAcc_Fecha
    on dbo.DespachoAccesorio (Fecha)
go

create index IX_RC_DespAcc_FechaIng
    on dbo.DespachoAccesorio (FechaIngreso)
go

create index IX_RC_DespAcc_FechaLimp
    on dbo.DespachoAccesorio (fechaLimpieza, UsuarioLimpiezaId)
go

create table dbo.Etiquetado
(
    Id          int identity
        constraint PK_Etiquetado
            primary key,
    serialId    int         not null,
    serial      varchar(50) not null,
    mac         varchar(50) not null,
    codigoSapId int         not null,
    nivelId     int         not null,
    loteId      int,
    variable1   varchar(30),
    variable2   varchar(30),
    variable3   varchar(30),
    variable4   varchar(30),
    estadoId    int         not null,
    reimpresion bit         not null,
    usuarioId   int         not null,
    fecha       datetime    not null
)
go

create table dbo.Forecast
(
    id            int identity
        constraint PK_Forecast
            primary key,
    linea_negocio nvarchar(255),
    fecha         date                             not null,
    fecha_entrega date,
    forecast_und  decimal(18, 2) default 0         not null,
    jornada       nvarchar(8)    default 'LV'      not null,
    dias_habiles  int,
    observaciones nvarchar(500),
    activo        bit            default 1         not null,
    usuario       nvarchar(150),
    created_at    datetime       default getdate() not null,
    updated_at    datetime       default getdate() not null,
    familias_json nvarchar(max),
    codigos_json  nvarchar(max)
)
go

create index IX_RC_Forecast_fecha
    on dbo.Forecast (fecha)
go

create table dbo.Inventario
(
    Id            int identity
        constraint PK_Inventario
            primary key,
    Serial        varchar(50)  not null,
    CodigoSap     varchar(20)  not null,
    CodigoSapReal varchar(20)  not null,
    Pallet        varchar(20)  not null,
    PalletReal    varchar(20)  not null,
    EstadoId      int          not null,
    EstadoSap     varchar(30)  not null,
    EstadoRR      varchar(30)  not null,
    Ajuste        varchar(200) not null,
    Fecha         datetime     not null,
    UsuarioId     int          not null,
    SerialId      int
        constraint UQ_SerialId
            unique,
    Mac           varchar(50),
    Sobrante      bit
        constraint DF_Inventario_Sobrante default 0
)
go

create table dbo.InventarioFaltante
(
    Id                         int identity
        constraint PK_InventarioFaltante
            primary key,
    SerialId                   int         not null,
    Serial                     varchar(50) not null,
    Mac                        varchar(50) not null,
    Serial3                    varchar(30),
    Serial4                    varchar(30),
    Serial5                    varchar(30),
    SmartCard                  varchar(30),
    CodigoSapId                int         not null,
    PalletId                   int         not null,
    PalletIdIngreso            int         not null,
    PalletIdAlmacen            int         not null,
    PalletIdEmpaque            int         not null,
    CajaEmpaqueId              int         not null,
    CajaDespachoId             int         not null,
    EstadoId                   int         not null,
    TipoOrigenId               int         not null,
    OrigenId                   int         not null,
    TipologiaId                int         not null,
    NivelId                    int         not null,
    Tramite                    varchar(30),
    Documento                  varchar(30),
    EmpresaTransportadoraId    int,
    Guia                       varchar(30),
    Caja                       int,
    Falla                      varchar(100),
    Causa                      varchar(100),
    Gaveta                     bit,
    Fuente                     bit,
    TecnicoCliente             varchar(50),
    PrealertaId                int         not null,
    Cruce                      bit         not null,
    Novedad                    varchar(40),
    GarantiaFabricante         bit,
    GarantiaWoden              bit,
    ObservacionId              int,
    Observaciones              varchar(50),
    UsuarioId                  int         not null,
    Fecha                      datetime    not null,
    Bodega                     varchar(20),
    EstadoCliente              varchar(20),
    EstadoEquipo               varchar(100),
    EstadoActual               varchar(100),
    UsuarioIdMovimiento        int         not null,
    FechaMovimiento            datetime    not null,
    transportadoraId           int,
    ciudadId                   int,
    loteId                     int,
    smartCardId                int,
    cajaIngresoId              int,
    numeroSmartcard            varchar(20),
    FechaInventarioTermina     datetime    not null,
    DocumentoInventarioTermina varchar(50) not null,
    UsuarioIdTermina           int         not null
)
go

create table dbo.InventarioTerminado
(
    Id               int identity
        constraint PK_InventarioTerminado
            primary key,
    Serial           varchar(50)  not null,
    CodigoSap        varchar(20)  not null,
    CodigoSapReal    varchar(20)  not null,
    Pallet           varchar(20)  not null,
    PalletReal       varchar(20)  not null,
    EstadoId         int          not null,
    EstadoSap        varchar(30)  not null,
    EstadoRR         varchar(30)  not null,
    Ajuste           varchar(200) not null,
    Fecha            datetime     not null,
    UsuarioId        int          not null,
    FechaTermina     datetime     not null,
    UsuarioIdTermina int          not null,
    Documento        varchar(50),
    SerialId         int,
    Mac              varchar(50),
    Sobrante         bit
        constraint DF_InventarioTer_Sobrante default 0
)
go

create table dbo.Lote
(
    id          int identity
        constraint PK_Lote
            primary key,
    nombre      varchar(50),
    descripcion varchar(120),
    activo      bit
)
go

create table dbo.ManoDeObra
(
    id             int identity
        primary key,
    fecha          date not null,
    detalle        varchar(50),
    observaciones  varchar(50),
    mano_obra_json nvarchar(max),
    activo         bit      default 1,
    usuario        nvarchar(150),
    created_at     datetime default getdate(),
    updated_at     datetime default getdate()
)
go

create table dbo.ManoDeObraUsuario
(
    id            int identity
        constraint PK_ManoDeObraUsuario
            primary key,
    usuarioId     int,
    manoDeObraId  int,
    usuarioActivo bit,
    altas         bit,
    bajas         bit
)
go

create table dbo.MasterImpresas
(
    id            int identity
        primary key,
    CajaEmpaqueId int not null,
    UsuarioId     int not null
)
go

create table dbo.Movimiento
(
    Id          int identity
        constraint PK_Movimientos
            primary key,
    SerialId    int          not null,
    Serial      varchar(50)  not null,
    OrigenId    int          not null,
    DestinoId   int          not null,
    Descripcion varchar(100) not null,
    UsuarioId   int          not null,
    Fecha       datetime     not null
)
go

create index idx_destinoId
    on dbo.Movimiento (DestinoId) include (SerialId)
go

create index Movimiento_Serial_IDX
    on dbo.Movimiento (Serial)
go

create index PalletMov
    on dbo.Movimiento (SerialId) include (OrigenId, DestinoId)
go

create table dbo.MovimientoAccesorio
(
    Id            int identity
        primary key,
    AccesorioId   int                        not null,
    TipoAccesorio varchar(20)                not null,
    CodigoSapId   int                        not null,
    OrigenId      int                        not null,
    DestinoId     int                        not null,
    PalletNumero  varchar(20),
    Caja          int,
    Descripcion   varchar(200),
    UsuarioId     int                        not null,
    Fecha         datetime default getdate() not null,
    PalletId      bigint                     not null
)
go

create table dbo.MovimientoLog
(
    Id          int identity,
    SerialId    int          not null,
    Serial      varchar(50)  not null,
    OrigenId    int          not null,
    DestinoId   int          not null,
    Descripcion varchar(100) not null,
    UsuarioId   int          not null,
    Fecha       datetime     not null
)
go

create table dbo.PalletLog
(
    Id                int         not null
        constraint PK_PalletLog
            primary key,
    Numero            varchar(20) not null,
    PosicionId        int         not null,
    CodigoSapId       int         not null,
    TipologiaId       int         not null,
    OrigenId          int         not null,
    DestinoId         int         not null,
    Tarjeta           varchar(20),
    UsuarioId         int         not null,
    Fecha             datetime    not null,
    UsuarioIdModifica int,
    FechaModifica     datetime,
    Activo            bit         not null
)
go

create table dbo.PrealertaConsolidada
(
    Id                     int identity
        primary key,
    PrealertaConsolidadaId int not null,
    PrealertaOrigenId      int not null,
    Fecha                  datetime default getdate()
)
go

create table dbo.SedeOrigen
(
    Id       int identity
        constraint PK_SedeOrigen
            primary key,
    SedeId   int not null,
    OrigenId int not null
)
go

create table dbo.SerialesDiscriminados
(
    Id       int identity,
    SerialId int           not null,
    Serial   nvarchar(100) not null
)
go

create table dbo.Servicios
(
    codigo      varchar(20)   not null
        primary key,
    descripcion nvarchar(100) not null
)
go

create table dbo.Contrato_Precio
(
    codigo_servicio    varchar(20)    not null
        constraint FK_ContratoPrecio_Servicios
            references dbo.Servicios
            on update cascade on delete cascade,
    codigo_contrato    varchar(20)    not null
        constraint FK_ContratoPrecio_Contrato
            references dbo.Contrato
            on update cascade on delete cascade,
    proceso            varchar(20)    not null,
    precio             decimal(22, 3) not null,
    fecha_creacion     datetime       not null,
    fecha_modificacion datetime,
    constraint PK_Contrato_Precio
        primary key (codigo_contrato, codigo_servicio)
)
go

create table dbo.Tarifa
(
    id        int identity
        constraint PK_Tarifa
            primary key,
    FamiliaId int,
    N0        decimal(12, 1),
    N1        decimal(12, 1),
    N2        decimal(12, 1),
    N3        decimal(12, 1),
    N4        decimal(12, 1),
    N5        decimal(12, 1),
    N6        decimal(12, 1)
)
go

create index Tarifa_FamiliaId
    on dbo.Tarifa (FamiliaId)
go

create table dbo.TempSeriales
(
    SerialEmpaque varchar(50)
)
go

create table dbo.TipoLectura
(
    id          int identity
        constraint PK_TipoLectura
            primary key,
    descripcion varchar(120) not null,
    rizquierda  int          not null,
    extrae      int          not null,
    rderecha    int          not null
)
go

create table dbo.TipoMaestro
(
    Id          int identity
        constraint PK_TiposMaestros
            primary key,
    Nombre      varchar(50)                        not null,
    Descripcion varchar(100)                       not null,
    Activo      bit
        constraint DF_TipoMaestro_Activo default 1 not null
)
go

create table dbo.Maestro
(
    Id            int identity
        constraint PK_Maestros
            primary key,
    Codigo        varchar(50)                  not null,
    Descripcion   varchar(100)                 not null,
    TipoMaestroId int                          not null
        constraint FK_Maestro_TipoMaestro
            references dbo.TipoMaestro
            on update cascade on delete cascade,
    Activo        bit
        constraint DF_Maestro_Activo default 1 not null,
    Detalle       varchar(120),
    adicional     int
)
go

create table dbo.CodigoSap
(
    Id              int identity
        constraint PK_CodigosSap
            primary key,
    Codigo          varchar(255),
    Descripcion     varchar(255),
    FamiliaId       int                          not null
        constraint FK_CodigoSap_MaestroFamilia
            references dbo.Maestro,
    TipoId          int                          not null
        constraint FK_CodigoSap_MaestroTipo
            references dbo.Maestro,
    Validacion      int,
    Direccion       varchar(255),
    Largos          varchar(255),
    Recorte         int,
    Reingreso       int                          not null,
    ClasificacionId int                          not null
        constraint FK_CodigoSap_MaestroClasificacion
            references dbo.Maestro,
    NumSerial       int
        constraint DF_CodigoSap_NumSerial default 0,
    Valor           int
        constraint DF_CodigoSap_Valor default 0,
    Activo          bit
        constraint DF_CodigoSap_Activo default 1 not null,
    idTipoLectura   int
        constraint FK_CodigoSap_TipoLectura
            references dbo.TipoLectura,
    AsignacionAcc   int,
    LargosMac       varchar(255),
    LargosSerial3   varchar(255),
    LargosSerial4   varchar(255),
    LargosSerial5   varchar(255),
    tipoEquipoId    int
        constraint FK_equipmentType_SapCode
            references dbo.Maestro,
    areaId          int
        constraint FK_area_SapCode
            references dbo.Maestro,
    modeloId        int
        constraint FK_model_sapcode
            references dbo.Maestro,
    proveedorId     int
        constraint FK_proveedor_sapcode
            references dbo.Maestro,
    validacionMac   int,
    direccionMac    varchar(255),
    recorteMac      int,
    AsignacionFalla int,
    multimodelo     int,
    cantidadCaja    int,
    Smartcard       bit
)
go

create index IX_RC_CodigoSap_FamiliaId
    on dbo.CodigoSap (FamiliaId)
go

create table dbo.CodigoSapFalla
(
    Id                    int identity
        constraint PK_CodigoSapFalla
            primary key,
    CodigoSapId           int not null
        constraint FK_CodigoSapFalla_CodigoSap
            references dbo.CodigoSap
            on update cascade on delete cascade,
    FallaId               int not null
        constraint FK_CodigoSapFalla_Maestro
            references dbo.Maestro
            on update cascade on delete cascade,
    ComponenteObligatorio bit
        constraint DF_CodigoSapFalla_Componente default 1
)
go

create table dbo.CodigoSapFallaComponente
(
    id               int identity
        constraint PK_CodigoSapFallaComponente
            primary key,
    codigosapFallaId int
        constraint FK_CodigoSapFallaComponente_CodigoSapFalla
            references dbo.CodigoSapFalla,
    componenteId     int
        constraint FK_CodigoSapFallaComponente_Componente
            references dbo.Maestro
)
go

create table dbo.CodigoSapImagen
(
    Id          int identity
        constraint PK_CodigoSapImagen
            primary key,
    CodigoSapId int   not null
        constraint FK_CodigoSapImagen_CodigoSapImagen
            references dbo.CodigoSap,
    Imagen      image not null
)
go

create table dbo.Diagnostico
(
    Id                  int identity
        constraint PK_Verificacion
            primary key,
    SerialId            int                               not null,
    Serial              varchar(50)                       not null,
    Mac                 varchar(50)                       not null,
    Serial3             varchar(50),
    CodigoSapId         int                               not null
        constraint FK_Diagnostico_CodigoSap
            references dbo.CodigoSap,
    CodigoSapIdAnterior int
        constraint DF_Diagnostico_CodigoSapIdAnterior default 0,
    EstadoFinalId       int
        constraint DF_Diagnostico_EstadoFinalId default 0 not null
        constraint FK_Diagnostico_MaestroEstadoFinal
            references dbo.Maestro,
    FallaId             int
        constraint DF_Diagnostico_FallaId default 0       not null
        constraint FK_Diagnostico_MaestroFalla
            references dbo.Maestro,
    UsuarioId           int                               not null,
    Observaciones       varchar(50),
    Fecha               datetime                          not null,
    EstadoCalidadId     int default 0,
    FechaCierre         datetime,
    variable1           varchar(50),
    variable2           varchar(50),
    variable3           varchar(50),
    variable4           varchar(50)
)
go

create index idx_serial
    on dbo.Diagnostico (Serial)
go

create index idx_mac
    on dbo.Diagnostico (Mac)
go

create index idx_fecha
    on dbo.Diagnostico (Fecha)
go

create index [<Diagnostico1>]
    on dbo.Diagnostico (Fecha) include (CodigoSapId)
go

create index [<Diagnostico>]
    on dbo.Diagnostico (CodigoSapId, Fecha)
go

create index [<Diagnostico4>]
    on dbo.Diagnostico (Fecha) include (CodigoSapId, EstadoFinalId)
go

create index IX_Diagnostico_SerialId_Id
    on dbo.Diagnostico (SerialId asc, Id desc) include (FallaId)
go

create table dbo.Ensamble
(
    Id                int identity
        primary key,
    SerialId          int         not null,
    Serial            varchar(50) not null,
    Mac               varchar(50) not null,
    Serial3           varchar(50),
    Serial4           varchar(50),
    Serial5           varchar(50),
    CodigoSapId       int
        constraint FK_CodigoSapId
            references dbo.CodigoSap,
    PalletId          int,
    Estadoid          int,
    TipologiaId       int
        constraint FK_Tipologiaid
            references dbo.Maestro,
    NivelId           int
        constraint FK_NivelId
            references dbo.Maestro,
    UsuarioId         int,
    UsuarioIdAsignado int,
    Fecha             datetime,
    LoteId            int,
    SmartCard         varchar(50),
    FechaCierre       datetime
)
go

create index [<Ensamble1>]
    on dbo.Ensamble (Fecha) include (CodigoSapId)
go

create index [<Ensamble2>]
    on dbo.Ensamble (CodigoSapId, Fecha)
go

create table dbo.Etiqueta
(
    Id          int identity
        constraint PK_Etiquetas
            primary key,
    Nombre      varchar(50)  not null,
    Tipo        varchar(150) not null,
    Impresion   int          not null,
    codigoSapId int
        constraint FK_CodigoSap_Etiqueta
            references dbo.CodigoSap,
    Activo      bit default 0
)
go

create table dbo.EtiquetaCampo
(
    Id         int identity
        constraint PK_EtiquetaCampo
            primary key,
    EtiquetaId int         not null
        constraint FK_EtiquetaCampo_Etiqueta
            references dbo.Etiqueta
            on update cascade on delete cascade,
    Nombre     varchar(20) not null,
    Valor      varchar(20) not null
)
go

create table dbo.Limpieza
(
    Id                    int identity
        constraint PK_Limpieza
            primary key,
    SerialId              int                                  not null,
    Serial                varchar(50)                          not null,
    Mac                   varchar(50)                          not null,
    Serial3               varchar(50),
    CodigoSapId           int                                  not null
        constraint FK_Limpieza_CodigoSap
            references dbo.CodigoSap,
    Asignacion_Confirmada bit
        constraint DF_Limpieza_Asignacion_Confirmada default 0 not null,
    UsuarioId             int                                  not null,
    UsuarioAsignadoId     int,
    Fecha                 datetime                             not null,
    FechaCierre           datetime
)
go

create index [NonClusteredIndex-20240505-172547]
    on dbo.Maestro (Id)
go

create table dbo.PQRS
(
    id                                int not null
        primary key,
    id_company                        int,
    company_name                      nvarchar(100),
    id_team                           int,
    team_name                         nvarchar(100),
    id_user                           int,
    user_name                         nvarchar(100),
    create_date                       smalldatetime,
    x_studio_area_responsable_id      int,
    x_studio_area_responsable_name    nvarchar(100),
    tipo_de_cliente_pqr_id            int,
    tipo_de_cliente_pqr_name          nvarchar(100),
    x_studio_placa                    nvarchar(50),
    refurbish_pqr_id                  int,
    refurbish_pqr_name                nvarchar(100),
    tecnologia_pqr_id                 int,
    tecnologia_pqr_name               nvarchar(100),
    ticket_ref                        int,
    description                       nvarchar(max),
    name                              nvarchar(100),
    x_studio_cantidad_de_equipos      int,
    clasificacion_refurbish_id        int,
    clasificacion_refurbish_name      nvarchar(100),
    x_studio_metodo_de_contacto       nvarchar(50),
    x_studio_fecha_llegada_unidades   smalldatetime,
    x_studio_fecha_reporte            smalldatetime,
    x_studio_metodo_de_contacto_final nvarchar(50),
    x_studio_tramite                  nvarchar(50),
    x_studio_nivel_pqr                nvarchar(50),
    partner_id                        int,
    partner_name                      nvarchar(100),
    partner_email                     nvarchar(100),
    attachment_url                    nvarchar(300),
    id_stage                          int,
    stage_name                        nvarchar(50),
    tag_ids                           nvarchar(50),
    x_studio_pais                     nvarchar(50),
    x_studio_observaciones            nvarchar(max),
    x_studio_diagnstico_tcnico_woden  nvarchar(max),
    Id_diagnostico                    int
        constraint FK_PQRS_Diagnostic
            references dbo.Diagnostico,
    SerialId                          int,
    CodigosapId                       int,
    FalladiagnosticoId                int,
    EstadoDiagnosticoId               int,
    FechaDiagnostico                  date
)
go

create index IX_PQRS_id
    on dbo.PQRS (id)
go

create table dbo.Parte
(
    Id                  int identity
        constraint PK_Parte
            primary key,
    ParteId             int not null
        constraint FK_Parte_MaestroParte
            references dbo.Maestro,
    CodigoSapId         int not null
        constraint FK_Parte_CodigoSap
            references dbo.CodigoSap,
    PalletId            int,
    EstadoId            int not null,
    EstadoCosmeticaId   int,
    Fecha               datetime,
    UsuarioId           int not null,
    UsuarioIdMovimiento int not null,
    FechaMovimiento     datetime
)
go

create table dbo.Posicion
(
    Id          int identity
        constraint PK_Posicion
            primary key,
    Numero      varchar(20)                     not null,
    CodigoSapId int                             not null
        constraint FK_Posicion_CodigoSap
            references dbo.CodigoSap,
    Activo      bit
        constraint DF_Posicion_Activo default 1 not null
)
go

create table dbo.Pallet
(
    Id                int identity
        constraint PK_Pallets
            primary key,
    Numero            varchar(50) not null,
    PosicionId        int         not null
        constraint FK_Pallet_Posicion
            references dbo.Posicion,
    CodigoSapId       int         not null
        constraint FK_Pallet_CodigoSap
            references dbo.CodigoSap,
    TipologiaId       int         not null
        constraint FK_Pallet_MaestroTipologia
            references dbo.Maestro,
    OrigenId          int         not null,
    DestinoId         int         not null,
    UsuarioId         int         not null,
    Fecha             datetime    not null,
    UsuarioIdModifica int
        constraint DF_Pallet_UsuarioIdModifica default 0,
    FechaModifica     datetime,
    EstadoInventario  int
        constraint DF_Pallet_EstadoInventario default 1,
    Activo            bit         not null,
    loteId            int
        constraint FK_Lote_Pallet
            references dbo.Lote
        constraint FK_Lote_PrealertaSerial
            references dbo.Lote
)
go

create table dbo.Accesorio
(
    Id                  int identity
        constraint PK_Accesorio
            primary key,
    CodigoSapId         int not null
        constraint FK_Accesorio_CodigoSap
            references dbo.CodigoSap,
    TipoAccesorio       varchar(255),
    TipoOrigenId        int not null
        constraint FK_Accesorio_MaestroTipoOrigen
            references dbo.Maestro,
    OrigenId            int not null
        constraint FK_Accesorio_MaestroOrigen
            references dbo.Maestro,
    PalletId            int not null
        constraint FK_Accesorio_Pallet
            references dbo.Pallet
            on update cascade on delete cascade,
    EstadoId            int not null,
    EstadoLimpiezaId    int
        constraint DF_Accesorio_EstadoLimpiezaId default 0,
    Documento           varchar(255),
    Observacion         varchar(255),
    Guia                varchar(255),
    UsuarioId           int not null,
    Fecha               varchar(255),
    SerialEmpaque       varchar(255)
        constraint DF_Accesorio_SerialEmpaque default 0,
    Caja                int,
    fecha_movimiento    datetime,
    previousPalletId    int,
    fechaLimpieza       varchar(255),
    fechaEmpaque        varchar(255),
    prealertaId         int,
    cruce               int,
    UsuarioLimpiezaId   int,
    UsuarioIdMovimiento int
)
go

create index IX_RC_Accesorio_PalletId
    on dbo.Accesorio (PalletId)
go

create index IX_RC_Accesorio_CodSap
    on dbo.Accesorio (CodigoSapId)
go

create index IX_RC_Accesorio_Fecha
    on dbo.Accesorio (Fecha)
go

create index IX_RC_Accesorio_FechaLimp
    on dbo.Accesorio (fechaLimpieza, UsuarioLimpiezaId)
go

create table dbo.CajaDespacho
(
    Id        int identity
        constraint PK_CajaDespacho
            primary key,
    Numero    varchar(255),
    PalletId  int not null
        constraint FK_CajaDespacho_Pallet
            references dbo.Pallet
            on update cascade on delete cascade,
    UsuarioId int not null,
    Fecha     varchar(255),
    Activo    int
)
go

create table dbo.CajaEmpaque
(
    Id        int identity
        constraint PK_Cajas
            primary key,
    Numero    varchar(255),
    PalletId  int not null
        constraint FK_CajaEmpaque_Pallet
            references dbo.Pallet
            on update cascade on delete cascade,
    EstadoId  int not null,
    UsuarioId int not null,
    Fecha     varchar(255),
    Activo    int
)
go

create unique index UX_CajaEmpaque_Numero_Pallet
    on dbo.CajaEmpaque (Numero, PalletId)
go

create table dbo.CajaIngreso
(
    Id        int identity
        constraint PK_CajaIngreso
            primary key,
    Numero    varchar(255),
    PalletId  int not null
        constraint FK_CajaIngreso_Pallet
            references dbo.Pallet,
    UsuarioId int not null,
    Fecha     varchar(255),
    Activo    int
)
go

create table dbo.Calidad
(
    Id               int identity
        constraint PK_Calidad
            primary key,
    SerialId         int      not null,
    Serial           varchar(255),
    Mac              varchar(255),
    CodigoSapId      int      not null
        constraint FK_Calidad_CodigoSap
            references dbo.CodigoSap,
    PalletId         int      not null
        constraint FK_Calidad_Pallet
            references dbo.Pallet,
    CajaEmpaqueId    int      not null,
    FallaCosmeticaId int
        constraint DF_Calidad_FallaCosmeticaId default 0
        constraint FK_Calidad_MaestroCosmetica
            references dbo.Maestro,
    FallaFuncionalId int
        constraint DF_Calidad_FallaFuncionalId default 0
        constraint FK_Calidad_MaestroFuncional
            references dbo.Maestro,
    EstadoFinalId    int
        constraint DF_Calidad_EstadoFinalId default 0
        constraint FK_Calidad_MaestroEstado
            references dbo.Maestro,
    TamanoLote       int,
    TamanoMuestra    int,
    UsuarioId        int      not null,
    Fecha            datetime not null,
    FechaCierre      datetime
)
go

create index idx_mac
    on dbo.Calidad (Mac)
go

create index idx_serial
    on dbo.Calidad (Serial)
go

create index idx_fecha
    on dbo.Calidad (Fecha)
go

create index [<ProcesosCalidadTel>]
    on dbo.Calidad (Fecha) include (CodigoSapId)
go

create index [<ProCalidad02Tel>]
    on dbo.Calidad (CodigoSapId, Fecha)
go

create table dbo.CiclicoPallet
(
    Id        int identity
        constraint PK_CiclicoPallet
            primary key,
    CiclicoId int not null
        constraint FK_CiclicoPallet_Ciclico
            references dbo.Ciclico,
    PalletId  int not null
        constraint FK_CiclicoPallet_Pallet
            references dbo.Pallet,
    Activo    int
)
go

create table dbo.Despacho
(
    Id                             int identity
        constraint PK_Despachos
            primary key,
    SerialId                       int         not null,
    Serial                         varchar(50) not null,
    Mac                            varchar(50) not null,
    Serial3                        varchar(50),
    SmartCard                      varchar(50),
    CodigoSapId                    int,
    PalletId                       int,
    PalletIdIngreso                int,
    CajaDespachoId                 int,
    EstadoId                       int,
    TipoOrigenId                   int,
    OrigenId                       int,
    TipologiaId                    int,
    NivelId                        int,
    Tramite                        varchar(30),
    Documento                      varchar(30),
    EmpresaTransportadoraId        int,
    EmpresaTransportadoraIngresoId int,
    Guia                           varchar(30),
    Falla                          varchar(100),
    Causa                          varchar(100),
    Gaveta                         bit,
    Fuente                         bit,
    PrealertaId                    int,
    Cruce                          bit         not null,
    Novedad                        varchar(50),
    PedidoSap                      varchar(50),
    DestinoInventario              varchar(20),
    UsuarioId                      int         not null,
    ObservacionId                  int,
    Observaciones                  varchar(50),
    EstadoEquipo                   varchar(100),
    EstadoActual                   varchar(100),
    Fecha                          datetime    not null,
    FechaIngreso                   datetime    not null,
    loteId                         int,
    smartCardId                    int,
    cajaIngresoId                  int
        constraint DF_Despacho_CajaIngresoId default 0
        constraint FK_Despacho_CajaIngreso
            references dbo.CajaIngreso,
    numeroSmartcard                varchar(20),
    FallaCosmeticaId               int,
    FallaFuncionalId               int,
    FechaCierre                    datetime
)
go

create index idx_serial
    on dbo.Despacho (Serial)
go

create index idx_mac
    on dbo.Despacho (Mac)
go

create index TEL
    on dbo.Despacho (SerialId) include (NivelId)
go

create index [DespachoTel01>]
    on dbo.Despacho (FechaIngreso) include (CodigoSapId)
go

create index [<DespachoTel02>]
    on dbo.Despacho (CodigoSapId, FechaIngreso)
go

create index [<DespachoTel04]
    on dbo.Despacho (Fecha) include (CodigoSapId)
go

create index [<TelDespacho03>]
    on dbo.Despacho (CodigoSapId, Fecha)
go

create table dbo.Empaque
(
    Id               int identity
        constraint PK_Empaque
            primary key,
    SerialId         int                        not null
        constraint Empaque_UNIQUE
            unique,
    Serial           varchar(50)                not null,
    Mac              varchar(50)                not null,
    CodigoSapId      int                        not null
        constraint FK_Empaque_CodigoSap
            references dbo.CodigoSap,
    PalletId         int                        not null
        constraint FK_Empaque_Pallet
            references dbo.Pallet,
    CajaEmpaqueId    int                        not null
        constraint FK_Empaque_CajaEmpaque
            references dbo.CajaEmpaque,
    NivelId          int
        constraint DF_Empaque_NivelId default 0 not null
        constraint FK_Empaque_MaestroNivel
            references dbo.Maestro,
    ClaseEmpaqueId   int,
    ObservacionId    int,
    CaracteristicaId int,
    FechaLote        date,
    UsuarioId        int                        not null,
    Fecha            datetime                   not null,
    loteId           int
        constraint FK_Lote_Empaque
            references dbo.Lote,
    smartCardId      int,
    smartCard        varchar(50),
    FechaCierre      datetime
)
go

create index idx_mac
    on dbo.Empaque (Mac)
go

create index idx_serial
    on dbo.Empaque (Serial)
go

create index idx_serialId
    on dbo.Empaque (SerialId)
go

create index [<Empaque021>]
    on dbo.Empaque (Fecha) include (CodigoSapId)
go

create index [<Empaque545>]
    on dbo.Empaque (CodigoSapId, Fecha)
go

create index IX_Empaque_SerialId
    on dbo.Empaque (SerialId)
go

create index IX_RC_Empaque_FechaCierre
    on dbo.Empaque (FechaCierre)
go

create index IX_RC_Empaque_PalletId
    on dbo.Empaque (PalletId)
go

create index idx_Pallet_Numero
    on dbo.Pallet (Numero)
go

create index IX_Pallet_Numero
    on dbo.Pallet (Numero)
go

create unique index UQ_Pallet_Numero_Activo
    on dbo.Pallet (Numero)
    where [Activo] = 1
go

create table dbo.Prealerta
(
    Id                    int identity
        constraint PK_Prealertas
            primary key,
    Nombre                varchar(50)            not null,
    TipoOrigenId          int                    not null
        constraint FK_Prealerta_MaestroTipoOrigen
            references dbo.Maestro,
    OrigenId              int                    not null
        constraint FK_Prealerta_MaestroOrigen
            references dbo.Maestro,
    Guia                  varchar(50)            not null,
    UsuarioId             int                    not null,
    Activo                bit
        constraint DF_Prealerta_Activo default 1 not null,
    Fecha                 datetime               not null,
    IdResponsable         int,
    IdResponsableRecogida int,
    Estado                varchar(20),
    tipologiaId           int
)
go

create table dbo.PrealertaSerial
(
    Id                int identity
        constraint PK_PrealertaSerial
            primary key,
    PrealertaId       int          not null
        constraint FK_PrealertaSerial_Prealerta
            references dbo.Prealerta
            on update cascade on delete cascade,
    Serial            varchar(50)  not null,
    Mac               varchar(50)  not null,
    CodigoSap         varchar(20)  not null,
    Descripcion       varchar(100),
    Cantidad          int          not null,
    Caja              int          not null,
    Falla             varchar(100) not null,
    TecnicoCliente    varchar(50)  not null,
    Pedido            nvarchar(30) not null,
    Tramite           nvarchar(30) not null,
    Novedad           varchar(100),
    Garantia          bit
        constraint DF_PrealertaSerial_Garantia default 0,
    en_recogida       tinyint,
    recogida          tinyint,
    cantidad_recogida int,
    tipo              varchar(20),
    serial_recorte    varchar(120),
    usuarioRecogeId   int,
    loteId            int,
    observaciones     varchar(120)
        constraint DF_PrealertaSerial_Observaciones default '0'
)
go

create table dbo.Presupuesto
(
    id            int identity
        constraint PK_Presupuesto
            primary key,
    fecha         date,
    segmentoId    int
        constraint DF_Presupuesto_SegmentoId default 0
        constraint FK_Presupuesto_Segmento
            references dbo.Maestro,
    nivelId       int
        constraint DF_Presupuesto_NivelId default 0
        constraint FK_Presupuesto_Nivel
            references dbo.Maestro,
    unidades      varchar(50),
    presupuesto   varchar(50),
    reclamo       varchar(50),
    diasHabiles   varchar(50),
    tipo          varchar(50),
    detalle       varchar(50),
    observaciones varchar(50)
)
go

create table dbo.Reparacion
(
    Id                  int identity
        constraint PK_Reparacion
            primary key,
    SerialId            int         not null,
    Serial              varchar(50) not null,
    Mac                 varchar(50) not null,
    Pcb                 varchar(50),
    CodigoSapId         int         not null
        constraint FK_Reparacion_CodigoSap
            references dbo.CodigoSap,
    EstadoFinalId       int         not null
        constraint FK_Reparacion_MaestroEstado
            references dbo.Maestro,
    FallaDxId           int         not null
        constraint FK_Reparacion_MaestroFallaFuncionalDx
            references dbo.Maestro,
    Falla1Id            int
        constraint DF_Reparacion_Falla1Id default 0
        constraint FK_Reparacion_MaestroFallaFuncional1
            references dbo.Maestro,
    Falla2Id            int
        constraint DF_Reparacion_Falla2Id default 0
        constraint FK_Reparacion_MaestroFallaFuncional2
            references dbo.Maestro,
    Falla3Id            int
        constraint DF_Reparacion_Falla3Id default 0
        constraint FK_Reparacion_MaestroFallaFuncional3
            references dbo.Maestro,
    Falla4Id            int
        constraint DF_Reparacion_Falla4Id default 0
        constraint FK_Reparacion_MaestroFallaFuncional4
            references dbo.Maestro,
    PartesCambiadas     varchar(300)
        constraint DF_Reparacion_PartesCambiadas default 0,
    TipoReparacionId    int,
    MotivoScrapId       int
        constraint DF_Reparacion_MotivoScrapId default 0
        constraint FK_Reparacion_MaestroMotivoScrap
            references dbo.Maestro,
    TecnicoAsignacionId int         not null,
    TecnicoReparacionId int
        constraint DF_Reparacion_TecnicoReparacionId default 0,
    FechaAsignacion     datetime    not null,
    FechaReparacion     datetime
        constraint DF_Reparacion_FechaReparacion default NULL,
    EstadoCalidadId     int default 0,
    UsuarioId           int,
    FechaCierre         datetime
)
go

create index idx_serial
    on dbo.Reparacion (Serial)
go

create index idx_mac
    on dbo.Reparacion (Mac)
go

create index [<ProcReparaci01Tel>]
    on dbo.Reparacion (FechaReparacion) include (CodigoSapId)
go

create index [<ReparacionTel01>]
    on dbo.Reparacion (FechaReparacion) include (CodigoSapId)
go

create index [<ReparacionTel02,>]
    on dbo.Reparacion (CodigoSapId, FechaReparacion)
go

create index [<ReparacionTelefonica>]
    on dbo.Reparacion (FechaReparacion) include (CodigoSapId, EstadoFinalId)
go

create index IX_RC_Reparacion_SerialId
    on dbo.Reparacion (SerialId, Id)
go

create table dbo.SerialComponente
(
    id           int identity
        constraint PK_SerialComponente
            primary key,
    SerialId     int          not null,
    Serial       varchar(120) not null,
    ComponenteId int          not null
        constraint FK_SerialComponente_ComponenteId
            references dbo.Maestro
)
go

create table dbo.SmartCard
(
    Id            int identity
        constraint PK_SmartCard
            primary key,
    SerialId      int                                   not null,
    Serial        varchar(120)                          not null,
    CodigoSapId   int                                   not null
        constraint FK_SmartCard_CodigoSap
            references dbo.CodigoSap,
    EstadoFinalId int
        constraint DF_SmartCard_EstadoFinalId default 0 not null
        constraint FK_SmartCard_MaestroEstadoFinal
            references dbo.Maestro,
    FallaId       int
        constraint DF_SemartCard_FallaId default 0      not null
        constraint FK_SmartCard_MaestroFalla
            references dbo.Maestro,
    UsuarioId     int                                   not null,
    Observaciones varchar(50),
    Fecha         datetime                              not null
)
go

create table dbo.Transportadora
(
    id        int identity
        constraint PK_Transportadora
            primary key,
    nombre    varchar(120),
    direccion varchar(120),
    telefono  varchar(20),
    activo    bit
)
go

create table dbo.Ingreso
(
    Id                      int identity
        constraint PK_Ingresos
            primary key,
    Serial                  varchar(50)                 not null
        constraint UNIQUE_SERIAL
            unique,
    Mac                     varchar(50)                 not null
        constraint UNIQUE_MAC
            unique,
    Serial3                 varchar(30),
    Serial4                 varchar(30),
    Serial5                 varchar(30),
    SmartCard               varchar(30),
    CodigoSapId             int                         not null
        constraint FK_Ingreso_CodigoSap
            references dbo.CodigoSap,
    PalletId                int
        constraint DF_Ingreso_PalletId default 0        not null
        constraint FK_Ingreso_Pallet
            references dbo.Pallet,
    PalletIdIngreso         int
        constraint DF_Ingreso_PalletIdIngreso default 0 not null
        constraint FK_Ingreso_PalletIngreso
            references dbo.Pallet,
    PalletIdAlmacen         int
        constraint DF_Ingreso_PalletIdAlmacen default 0 not null
        constraint FK_Ingreso_PalletAlmacen
            references dbo.Pallet,
    PalletIdEmpaque         int
        constraint DF_Ingreso_PalletIdEmpaque default 0 not null
        constraint FK_Ingreso_PalletEmpaque
            references dbo.Pallet,
    CajaEmpaqueId           int
        constraint DF_Ingreso_CajaEmpaqueId default 0   not null
        constraint FK_Ingreso_CajaEmpaque
            references dbo.CajaEmpaque,
    CajaDespachoId          int
        constraint DF_Ingreso_CajaDespachoId default 0  not null
        constraint FK_Ingreso_CajaDespacho
            references dbo.CajaDespacho,
    EstadoId                int                         not null
        constraint CK_Ingreso_EstadoID
            check ([EstadoID] <> 0),
    TipoOrigenId            int
        constraint DF_Ingreso_TipoOrigenId default 0    not null
        constraint FK_Ingreso_MaestroTipoOrigen
            references dbo.Maestro
        constraint CK_Ingreso_TipoOrigenID
            check ([TipoOrigenID] <> 0),
    OrigenId                int
        constraint DF_Ingreso_OrigenId default 0        not null
        constraint FK_Ingreso_MaestroOrigen
            references dbo.Maestro
        constraint CK_Ingreso_OrigenID
            check ([OrigenID] <> 0),
    TipologiaId             int
        constraint DF_Ingreso_TipologiaId default 0     not null
        constraint FK_Ingreso_MaestroTipologia
            references dbo.Maestro
        constraint CK_Ingreso_TipologiaID
            check ([TipologiaID] <> 0),
    NivelId                 int                         not null
        constraint FK_Ingreso_Maestro
            references dbo.Maestro,
    Tramite                 varchar(30),
    Documento               varchar(30),
    EmpresaTransportadoraId int,
    Guia                    varchar(30),
    Caja                    int,
    Falla                   varchar(100),
    Causa                   varchar(100),
    Gaveta                  bit,
    Fuente                  bit,
    TecnicoCliente          varchar(50),
    PrealertaId             int
        constraint DF_Ingreso_PrealertaId default 0     not null
        constraint FK_Ingreso_Prealerta
            references dbo.Prealerta,
    Cruce                   bit
        constraint DF_Ingreso_Cruce default 0           not null,
    Novedad                 varchar(40),
    GarantiaFabricante      bit,
    GarantiaWoden           bit,
    ObservacionId           int
        constraint DF_Ingreso_ObservacionId default 197
        constraint FK_Ingreso_MaestroObservacionesIngreso
            references dbo.Maestro,
    Observaciones           varchar(50),
    UsuarioId               int                         not null,
    Fecha                   datetime                    not null,
    Bodega                  varchar(20),
    EstadoCliente           varchar(20),
    EstadoEquipo            varchar(100),
    EstadoActual            varchar(100),
    UsuarioIdMovimiento     int                         not null,
    FechaMovimiento         datetime                    not null,
    transportadoraId        int
        constraint FK_carrier_entry
            references dbo.Transportadora,
    ciudadId                int
        constraint FK_city_entry
            references dbo.Ciudad,
    loteId                  int
        constraint FK_lote_entry
            references dbo.Lote,
    smartCardId             int,
    cajaIngresoId           int
        constraint DF_Ingreso_CajaIngresoId default 0
        constraint FK_Ingreso_CajaIngreso
            references dbo.CajaIngreso,
    numeroSmartcard         varchar(20),
    modeloId                int
        constraint DF_Ingreso_ModeloId default 0
        constraint FK_Ingreso_MaestroModelo
            references dbo.Maestro,
    FallaCosmeticaId        int,
    FallaFuncionalId        int,
    ClienteId               int
)
go

create table dbo.Ilegible
(
    Id                  int identity
        constraint PK_Ilegible
            primary key,
    SerialId            int                     not null
        constraint FK_Ilegible_Ingreso
            references dbo.Ingreso,
    ConsecutivoSerial   varchar(50)             not null,
    ConsecutivoMac      varchar(50)             not null,
    SerialConfirmacion  varchar(50),
    MacConfirmacion     varchar(50),
    Confirmado          bit
        constraint DF_Ilegible_Estado default 0 not null,
    Fecha               datetime                not null,
    Serial3Confirmacion varchar(50),
    Serial4Confirmacion varchar(50),
    Serial5Confirmacion varchar(50)
)
go

exec sp_addextendedproperty 'MS_Description', '0:No Confirmado, 1:Confirmado', 'SCHEMA', 'dbo', 'TABLE', 'Ilegible',
     'COLUMN', 'Confirmado'
go

create index idx_mac
    on dbo.Ingreso (Mac)
go

create index idx_serial
    on dbo.Ingreso (Serial)
go

create index [<Name of Missing Index, sysname,>]
    on dbo.Ingreso (EstadoId) include (Serial, CodigoSapId, PalletId, TipoOrigenId, OrigenId, TipologiaId, NivelId,
                                       GarantiaFabricante, Fecha, FechaMovimiento, loteId)
go

create index SmartSap
    on dbo.Ingreso (CodigoSapId) include (Serial, PalletId, EstadoId, TipoOrigenId, OrigenId, TipologiaId, NivelId,
                                          GarantiaFabricante, Fecha, FechaMovimiento, loteId)
go

create index Wip_Unitario
    on dbo.Ingreso (CodigoSapId) include (Serial, Mac, PalletId, EstadoId, TipoOrigenId, OrigenId, TipologiaId, NivelId,
                                          GarantiaFabricante, Fecha, FechaMovimiento, loteId)
go

create index idx_estadoid
    on dbo.Ingreso (EstadoId)
go

create index idx_palletid
    on dbo.Ingreso (PalletId)
go

create index idx_Ingreso_Serial
    on dbo.Ingreso (Serial)
go

create index Retorno
    on dbo.Ingreso (CodigoSapId) include (Serial, Mac, Serial3, Serial4, Serial5, TipoOrigenId, TipologiaId, Fecha,
                                          EstadoCliente, loteId, modeloId)
go

create index [<IngresoTel01>]
    on dbo.Ingreso (Fecha) include (CodigoSapId)
go

create index [<IngresoTEL02>]
    on dbo.Ingreso (CodigoSapId, Fecha)
go

create index [<Scrap1>]
    on dbo.Ingreso (Fecha) include (CodigoSapId, TipologiaId)
go

create index [<Scrap2>]
    on dbo.Ingreso (CodigoSapId, Fecha) include (TipologiaId)
go

create index IX_Ingreso_PalletId
    on dbo.Ingreso (PalletId)
go

create index IX_Ingreso_Serial
    on dbo.Ingreso (Serial)
go

create index IX_RC_Ingreso_TipologiaId
    on dbo.Ingreso (TipologiaId)
go

create table dbo.UsuarioSys
(
    id             int          not null
        primary key,
    nombres        varchar(120) not null,
    nombre_usuario varchar(20)  not null,
    perfil_id      int,
    Identificacion bigint
)
go

create table dbo.WorkFlow
(
    Id          int identity
        constraint PK_Destino
            primary key,
    ModuloId    int not null,
    OrigenId    int not null,
    OpcionId    int not null,
    TipologiaId int not null,
    nivelId     int
        constraint DF_WorkFlow_NivelId default 0
)
go

create table dbo.WorkFlowDestino
(
    Id         int identity
        constraint PK_WorkFlowDestino
            primary key,
    WorkFlowId int not null
        constraint FK_WorkFlowDestino_WorkFlow
            references dbo.WorkFlow
            on update cascade on delete cascade,
    DestinoId  int not null
)
go

create table dbo.catl
(
    nombre      varchar(50) not null
        primary key,
    descripcion varchar(50) not null
)
go

create table dbo.catlobj
(
    nombrecatl  varchar(50) not null
        constraint fk_catl_catlobj
            references dbo.catl,
    nombre      varchar(30) not null,
    descripcion varchar(50) not null,
    tipo        varchar(20) default NULL,
    primary key (nombrecatl, nombre)
)
go

create table dbo.catlobjxus
(
    nombrecatl varchar(50) not null,
    nombre     varchar(20) not null,
    usuario    varchar(20) not null,
    accion     varchar(15) not null,
    primary key (nombrecatl, nombre, usuario)
)
go

create table dbo.catlxus
(
    nombre  varchar(50) not null,
    usuario varchar(20) not null,
    ingreso varchar(5)  not null,
    primary key (nombre, usuario)
)
go

create table dbo.menu
(
    id              varchar(20) not null
        constraint PK_menu
            primary key,
    descripcion     varchar(120),
    tipo            varchar(20),
    orden           int,
    accion          varchar(200),
    estado          varchar(20),
    icono           varchar(50),
    id_padre        varchar(20),
    view_name       varchar(100),
    controller_name varchar(100)
)
go

create table dbo.menu_perfil
(
    IdMenu   varchar(20) not null
        constraint FK_menu_perfil_menu
            references dbo.menu,
    IdPerfil int         not null,
    constraint PK_menu_perfil
        primary key (IdMenu, IdPerfil)
)
go

create table dbo.tmp_proceso
(
    Serial varchar(50)
)
go

CREATE VIEW [dbo].[Cliente]
AS
SELECT        Id, Nombre, ColorCorporativo, Activo
FROM            WmsWdGeneral.dbo.Cliente AS Cliente

go

exec sp_addextendedproperty 'MS_DiagramPane1', N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "Cliente"
            Begin Extent = 
               Top = 6
               Left = 38
               Bottom = 136
               Right = 247
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
      Begin ColumnWidths = 9
         Width = 284
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
', 'SCHEMA', 'dbo', 'VIEW', 'Cliente'
go

exec sp_addextendedproperty 'MS_DiagramPaneCount', 1, 'SCHEMA', 'dbo', 'VIEW', 'Cliente'
go


CREATE VIEW [dbo].[Estado]
AS
SELECT        Id, Nombre, Descripcion, ModuloId, Icono, Activo,PadreInventario
FROM            WmsWdGeneral.dbo.Estado AS Estado_1
go

exec sp_addextendedproperty 'MS_DiagramPane1', N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "Estado_1"
            Begin Extent = 
               Top = 6
               Left = 38
               Bottom = 135
               Right = 208
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
      Begin ColumnWidths = 9
         Width = 284
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
', 'SCHEMA', 'dbo', 'VIEW', 'Estado'
go

exec sp_addextendedproperty 'MS_DiagramPaneCount', 1, 'SCHEMA', 'dbo', 'VIEW', 'Estado'
go

CREATE VIEW [dbo].[Modulo]
AS
SELECT        Id, SeccionId, Nombre, Icono, Controlador
FROM            WmsWdGeneral.dbo.Modulo AS Modulo

go

exec sp_addextendedproperty 'MS_DiagramPane1', N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "Modulo"
            Begin Extent = 
               Top = 6
               Left = 38
               Bottom = 135
               Right = 208
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
', 'SCHEMA', 'dbo', 'VIEW', 'Modulo'
go

exec sp_addextendedproperty 'MS_DiagramPaneCount', 1, 'SCHEMA', 'dbo', 'VIEW', 'Modulo'
go

CREATE VIEW [dbo].[Permiso]
AS
SELECT        Id, ModuloId, Nombre, Descripcion
FROM            WmsWdGeneral.dbo.Permiso AS Permiso

go

exec sp_addextendedproperty 'MS_DiagramPane1', N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "Permiso"
            Begin Extent = 
               Top = 6
               Left = 38
               Bottom = 136
               Right = 247
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
      Begin ColumnWidths = 9
         Width = 284
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
', 'SCHEMA', 'dbo', 'VIEW', 'Permiso'
go

exec sp_addextendedproperty 'MS_DiagramPaneCount', 1, 'SCHEMA', 'dbo', 'VIEW', 'Permiso'
go

CREATE VIEW [dbo].[Usuario]
AS
SELECT        Id, Identificacion, Nombres, Apellidos, NombreUsuario, Clave, FechaNacimiento, Correo, CargoId, AreaId, FechaCreacion, FechaUltimoAcceso, Ip, SedeId, 
                         Activo
FROM            WmsWdGeneral.dbo.Usuario AS Usuario_1

go

exec sp_addextendedproperty 'MS_DiagramPane1', N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[40] 4[20] 2[20] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "Usuario_1"
            Begin Extent = 
               Top = 6
               Left = 38
               Bottom = 135
               Right = 232
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
      Begin ColumnWidths = 9
         Width = 284
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
         Width = 1500
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
', 'SCHEMA', 'dbo', 'VIEW', 'Usuario'
go

exec sp_addextendedproperty 'MS_DiagramPaneCount', 1, 'SCHEMA', 'dbo', 'VIEW', 'Usuario'
go

-- dbo.vw_IngresoVsEmpaque source

CREATE VIEW vw_IngresoVsEmpaque AS
SELECT 
    COALESCE(i.Id, e.SerialId) AS SerialId,
    i.Id AS IngresoId,
    i.serial AS serialIngreso,
    e.SerialId AS EmpaqueSerialId,
    e.serial AS serialEmpaque,
    i.PalletId,
    i.CajaEmpaqueId,
    CASE 
        WHEN i.Id IS NULL THEN 'Solo Empaque'
        WHEN e.SerialId IS NULL THEN 'Solo Ingreso'
        ELSE 'OK'
    END AS Estado
FROM Ingreso i
FULL OUTER JOIN Empaque e
  ON i.Id = e.SerialId
 AND i.PalletId = e.PalletId
 AND i.CajaEmpaqueId = e.CajaEmpaqueId;
go

CREATE PROCEDURE InsertToUsuarioSys
	AS
	BEGIN
	INSERT INTO UsuarioSys (id,nombres,nombre_usuario,perfil_id,Identificacion)
	select  DISTINCT a.Id,a.nombres,a.NombreUsuario,a.PerfilId, a.Identificacion from (
		select u.Id,u.Nombres + ' ' + u.Apellidos nombres, u.nombreUsuario,ucp.PerfilId, u.Identificacion from WmsWdGeneral.dbo.UsuarioClientePerfil ucp 
		inner join WmsWdGeneral.dbo.UsuarioCliente uc on ucp.usuarioClienteId=uc.id 
		right join WmsWdGeneral.dbo.Usuario u on uc.usuarioId=u.id 	
		where ucp.perfilId IS NULL  OR (NombreUsuario = 'adminCreador' AND  uc.ClienteId = (select id FROM WmsWdGeneral.dbo.Cliente where dbase = (SELECT DB_NAME())))) a left join UsuarioSys b on a.Id = b.id where b.nombre_usuario is null
	END
go


CREATE   FUNCTION dbo.fn_GetDescripcionMovimiento(
    @EstadoDestino VARCHAR(50),
    @EstadoOrigen VARCHAR(50),
    @Novedad VARCHAR(50),
    @Observaciones VARCHAR(150),
    @Posicion VARCHAR(20),
    @Pallet VARCHAR(20),
    @Nivel VARCHAR(1),
    @Tecnico VARCHAR(50),
    @EstadoReparacion VARCHAR(20),
    @FallaDiagnostico VARCHAR(20),
    @CajaEmpaque VARCHAR(50),
    @CajaEmpaqueAnterior VARCHAR(50),
    @PalletEmpaqueAnterior VARCHAR(20),
    @Tipologia VARCHAR(50),
    @CodigoSap VARCHAR(15),
    @CodigoSapAnterior VARCHAR(15),
    @Serial VARCHAR(50)
)
RETURNS VARCHAR(500)
AS
BEGIN
    DECLARE @Descripcion VARCHAR(500);
    
    -- CASO 1: Cambio de estado
    IF(@EstadoDestino <> @EstadoOrigen)
    BEGIN
        IF(@EstadoDestino='INGRESO' OR @EstadoDestino='ILEGIBLE' OR @EstadoDestino='SERIALIZAR')
        BEGIN
            IF(@EstadoOrigen='TRANSITO')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Pallet Abierto');
            ELSE IF(@EstadoOrigen='NOVEDAD')
                SET @Descripcion = 'Ingreso de Serial Regularizado';
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='P-INGRESO')
        BEGIN
            SET @Descripcion = CONCAT('Envío a P-INGRESO, Serial Regularizado: ', @Observaciones);
        END
        ELSE IF(@EstadoDestino='TRANSITO')
        BEGIN
            IF(@EstadoOrigen='INGRESO')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Pallet Cerrado');
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='P-UNIFICACION')
        BEGIN
            IF(@EstadoOrigen='SEPARACION' OR @EstadoOrigen='ILEGIBLE' OR @EstadoOrigen='SERIALIZAR')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Posición: ', @Posicion);
            ELSE IF(@EstadoOrigen='UNIFICACION')
                SET @Descripcion = CONCAT('Equipo Removido de: ', @EstadoOrigen, ', Posición: ', @Posicion);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='UNIFICACION')
        BEGIN
            IF(@EstadoOrigen='UNIFICACION-ENTREGA')
                SET @Descripcion = CONCAT('Equipo Removido de: ', @EstadoOrigen);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='UNIFICACION-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='UNIFICACION')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Pallet: ', @Pallet);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='SEPARACION-NOVEDAD')
        BEGIN
            IF(@EstadoOrigen='SEPARACION-ENTREGA')
                SET @Descripcion = CONCAT('Equipo Removido de: ', @EstadoOrigen);
            ELSE
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Novedad: ', @Novedad);
        END
        ELSE IF(@EstadoDestino='SEPARACION-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='SEPARACION-NOVEDAD')
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Pallet: ', @Pallet);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='INVENTARIO')
        BEGIN
            SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Pallet Abierto');
        END
        ELSE IF(@EstadoDestino='ALMACENAMIENTO')
        BEGIN
            IF(@EstadoOrigen='INVENTARIO')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Pallet Cerrado');
            ELSE IF(@EstadoOrigen='P-ALMACENAMIENTO')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Posición: ', @Posicion);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='ALMACENAMIENTO-NOVEDAD')
        BEGIN
            IF(@EstadoOrigen='ALMACENAMIENTO-ENTREGA')
                SET @Descripcion = CONCAT('Equipo Removido de: ', @EstadoOrigen);
            ELSE
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Novedad: ', @Novedad);
        END
        ELSE IF(@EstadoDestino='ALMACENAMIENTO-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='ALMACENAMIENTO-NOVEDAD')
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Pallet: ', @Pallet);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='NOVEDAD-TERMINADO')
        BEGIN
            SET @Descripcion = CONCAT('Serial regularizado, ', @Observaciones);
        END
        ELSE IF(@EstadoDestino='CLASIFICACION')
        BEGIN
            IF(@EstadoOrigen='EMPACADO' OR @EstadoOrigen='EMPAQUE')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', por Reemplazo');
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='CLASIFICACION-REPARACION')
        BEGIN
            IF(@EstadoOrigen='CLASIFICACION')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Nivel: ', @Nivel);
            ELSE IF(@EstadoOrigen='CLASIFICACION-ENTREGA-REPARACION')
                SET @Descripcion = CONCAT('Equipo Removido de: ', @EstadoOrigen);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='CLASIFICACION-ENTREGA-REPARACION')
        BEGIN
            IF(@EstadoOrigen='CLASIFICACION-REPARACION')
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Pallet: ', @Pallet, ' Mal estado, Falla:OXIDO');
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='ENSAMBLE')
        BEGIN
            IF(@EstadoOrigen='CLASIFICACION')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Nivel: ', @Nivel);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='COSMETICA')
        BEGIN
            IF(@EstadoOrigen='COSMETICA-ENTREGA')
                SET @Descripcion = CONCAT('Equipo Removido de: ', @EstadoOrigen);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='COSMETICA-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='COSMETICA')
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Pallet: ', @Pallet);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='ENSAMBLE-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='ENSAMBLE')
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Pallet: ', @Pallet);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='ETIQUETADO')
        BEGIN
            IF(@EstadoOrigen='CLASIFICACION')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Nivel: ', @Nivel);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='REPARACION-ASIGNADO')
        BEGIN
            SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Tecnico Asignado: ', @Tecnico);
        END
        ELSE IF(@EstadoDestino='REPARACION')
        BEGIN
            SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Estado Reparacion: ', @EstadoReparacion);
        END
        ELSE IF(@EstadoDestino='REPARACION-TERMINADO')
        BEGIN
            IF(@EstadoOrigen='REPARACION-ENTREGA')
                SET @Descripcion = CONCAT('Equipo Removido de: ', @EstadoOrigen);
            ELSE IF(@EstadoOrigen='REPARACION')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Reparación Terminada');
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='REPARACION-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='REPARACION-TERMINADO')
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Pallet: ', @Pallet);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='DIAGNOSTICO')
        BEGIN
            IF(@EstadoOrigen='EMPACADO' OR @EstadoOrigen='EMPAQUE')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', por Reemplazo');
            ELSE IF(@EstadoOrigen='CLASIFICACION')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Nivel: ', @Nivel);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='DIAGNOSTICO-TERMINADO')
        BEGIN
            IF(@EstadoOrigen='DIAGNOSTICO-ENTREGA')
                SET @Descripcion = CONCAT('Equipo Removido de: ', @EstadoOrigen);
            ELSE IF(@EstadoOrigen='DIAGNOSTICO')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Mal Estado, Falla: ', @FallaDiagnostico);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='DIAGNOSTICO-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='DIAGNOSTICO-TERMINADO')
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Pallet: ', @Pallet);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='EMPAQUE')
        BEGIN
            IF(@EstadoOrigen='DIAGNOSTICO')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Buen Estado');
            ELSE IF(@EstadoOrigen='EMPACADO')
                SET @Descripcion = CONCAT('Equipo Desempacado, Pallet: ', @PalletEmpaqueAnterior, ', Caja: ', @CajaEmpaqueAnterior);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='EMPACADO')
        BEGIN
            IF(@EstadoOrigen='EMPAQUE')
                SET @Descripcion = CONCAT('Equipo Empacado, Pallet: ', @Pallet, ', Caja: ', @CajaEmpaque);
            ELSE IF(@EstadoOrigen IN ('EMPAQUE-ENTREGA', 'EMPAQUE-RECHAZO COSMETICO', 'EMPAQUE-RECHAZO FUNCIONAL', 'EMPAQUE-APROBADO'))
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Pallet Abierto');
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='EMPAQUE-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='EMPAQUE')
                SET @Descripcion = 'Envío a EMPAQUE-ENTREGA, Desde: EMPAQUE, Pallet Cerrado';
            ELSE IF(@EstadoOrigen='CALIDAD-ENTREGA')
            BEGIN
                IF(@Tipologia='EMPAQUE APROBADO')
                    SET @Descripcion = CONCAT('Envío a EMPAQUE-ENTREGA, Desde: ', @EstadoOrigen, ', Pallet Aprobado');
                ELSE IF(@Tipologia='EMPAQUE RECHAZO FUNCIONAL')
                    SET @Descripcion = CONCAT('Envío a EMPAQUE-ENTREGA, Desde: ', @EstadoOrigen, ', Rechazo Funcional');
                ELSE IF(@Tipologia='EMPAQUE RECHAZO COSMETICO')
                    SET @Descripcion = CONCAT('Envío a EMPAQUE-ENTREGA, Desde: ', @EstadoOrigen, ', Rechazo Cosmetico');
                ELSE
                    SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
            END
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='P-CALIDAD')
        BEGIN
            IF(@EstadoOrigen='CALIDAD')
                SET @Descripcion = CONCAT('Equipo Removido de: ', @EstadoOrigen);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='CALIFICADO')
        BEGIN
            SET @Descripcion = 'Ingreso en CALIDAD';
        END
        ELSE IF(@EstadoDestino='CALIDAD-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='CALIFICADO')
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Pallet: ', @Pallet);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='PREDESPACHO')
        BEGIN
            IF(@EstadoOrigen IN ('PREDESPACHO-ENTREGA', 'DESPACHO-ENTREGA'))
                SET @Descripcion = CONCAT('Equipo Removido de: ', @EstadoOrigen);
            ELSE IF(@EstadoOrigen='PREDESPACHADO')
                SET @Descripcion = CONCAT('Equipo Eliminado, Pallet Destino: ', @Pallet, ', Caja Destino: ', @CajaEmpaque);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='PREDESPACHO-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='PREDESPACHO')
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Pallet: ', @Pallet);
            ELSE IF(@EstadoOrigen='DESPACHO')
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Pallet Abierto');
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='DESPACHO')
        BEGIN
            SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen, ', Pallet: ', @Pallet);
        END
        ELSE IF(@EstadoDestino='DESPACHO-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='PREDESPACHO')
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Pallet: ', @Pallet);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='NOVEDAD-TERMINADO')
        BEGIN
            IF(@EstadoOrigen='NOVEDAD-ENTREGA')
                SET @Descripcion = CONCAT('Equipo Removido de: ', @EstadoOrigen);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE IF(@EstadoDestino='NOVEDAD-ENTREGA')
        BEGIN
            IF(@EstadoOrigen='NOVEDAD-TERMINADO')
                SET @Descripcion = CONCAT('Ingreso en ', @EstadoDestino, ', Pallet: ', @Pallet);
            ELSE
                SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
        ELSE
        BEGIN
            SET @Descripcion = CONCAT('Envío a ', @EstadoDestino, ', Desde: ', @EstadoOrigen);
        END
    END
    -- CASO 2: Mismo estado (actualizaciones sin cambio de estado)
    ELSE
    BEGIN
        IF(@EstadoDestino='ALMACENAMIENTO' AND @EstadoOrigen='ALMACENAMIENTO')
            SET @Descripcion = CONCAT('Equipo Agrupado, Pallet: ', @Pallet, ', Posicion: ', @Posicion);
        ELSE IF(@EstadoOrigen='DIAGNOSTICO' AND @CodigoSap <> @CodigoSapAnterior)
            SET @Descripcion = CONCAT('Actualizacion de firmware, de: ', @CodigoSapAnterior, ' a: ', @CodigoSap);
        ELSE IF(@EstadoDestino='TRANSITO' AND @EstadoOrigen='TRANSITO')
            SET @Descripcion = CONCAT('Serial Regularizado: ', @Observaciones);
        ELSE IF(@EstadoDestino='DIAGNOSTICO-TERMINADO' AND @EstadoOrigen='DIAGNOSTICO-TERMINADO')
            SET @Descripcion = CONCAT('Actualizacion de Serial Trocado, Serial Real: ', @Serial);
        ELSE
            SET @Descripcion = CONCAT('Actualización en ', @EstadoDestino);
    END
    
    RETURN @Descripcion;
END
go


/**
 * Author:  Pedro Neira
 * Created: DIC, 20 2023
 */

CREATE PROCEDURE [dbo].[pa_AddCiclicPallet]
@CiclicoId INT,
@PalletId INT
AS

BEGIN
	BEGIN TRY
		BEGIN TRANSACTION
			INSERT INTO CiclicoPallet(CiclicoId,PalletId,Activo)
			VALUES(@CiclicoId,@PalletId,1)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH;
END
go

CREATE PROCEDURE [dbo].[pa_AddCountBoxFamily]
        @Value varchar(10),
        @Id int,
        @Filas int output
        AS

        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.

            UPDATE Maestro SET adicional=@Value 
            WHERE id=@Id
            select @Filas=@@ROWCOUNT
        END
go

CREATE PROCEDURE [dbo].[pa_AddCountPalletFamily]
        @Value varchar(10),
        @Id int,
        @Filas int output
        AS

        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.

            UPDATE Maestro SET Detalle=@Value 
            WHERE id=@Id
            select @Filas=@@ROWCOUNT
        END
go


-----------------------------------------------------------------------------------------------------------------------
-- Retorno de accesorios mal Tipificados. 


CREATE   PROCEDURE [dbo].[pa_BackCleanEntry]
    @Id VARCHAR(50),
    @Filas  INT OUTPUT
AS
BEGIN
        UPDATE dbo.accesorio
        SET estadoid=95,estadolimpiezaid=0,fecha_movimiento=getdate(),fechalimpieza=null,usuariolimpiezaid=null
        WHERE id =@id 
END
go

CREATE   PROCEDURE [dbo].[pa_BackRepairedEntry]
    @Serial VARCHAR(50),
    @Filas  INT OUTPUT
AS
BEGIN
 
DECLARE @EstadoAsignadoId INT;
DECLARE @EstadoIdIng INT;
DECLARE @NivelOrigenid INT;
 
SELECT TOP (1) @EstadoAsignadoId = id FROM Maestro
WHERE Codigo = 'ASIGNADO';
 
SELECT TOP (1) @EstadoIdIng = id FROM Estado
WHERE Nombre = 'REPARACION-ASIGNADO';
 
SELECT TOP (1) @NivelOrigenid = id FROM Maestro
WHERE codigo = '1' and TipoMaestroId=(select top 1 id from TipoMaestro where Nombre='Niveles');
 
        BEGIN TRAN
        UPDATE dbo.Reparacion
        SET estadoFinalId= @EstadoAsignadoId,falla1Id=0,falla2Id= 0,falla3Id= 0,partesCambiadas= NULL,tecnicoReparacionId= 0,
		fechaReparacion= NULL,EstadoCalidadId= 0,MotivoScrapId=0
        WHERE id = (SELECT MAX(r.id) FROM dbo.Reparacion r WHERE r.serial = @Serial);
		SET @Filas = @@ROWCOUNT;
 
        UPDATE dbo.Ingreso
        SET estadoId = @EstadoIdIng,NivelId=@NivelOrigenid
        WHERE serial = @Serial;
		COMMIT TRAN
END
-----------------------------------------------------------------------------------------------------------------------
go

CREATE PROCEDURE [dbo].[pa_BoxEntry]
        @PalletId INT
        AS

        BEGIN

            SET NOCOUNT ON;

            SELECT ci.id,ci.numero,count(ing.id)seriales FROM CajaIngreso ci 
            LEFT JOIN INGRESO ing ON ci.id=ing.cajaIngresoId 
            WHERE ci.palletId=@PalletId and ci.activo=1  
            GROUP BY ci.id,ci.numero 
            ORDER BY ci.numero

        END
go


CREATE   PROCEDURE [dbo].[pa_BulkInsertEntry]
    @json NVARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO Ingreso (
        serial, mac, serial3, serial4, serial5, codigoSapId,
        palletId, palletIdIngreso, estadoId, tipoOrigenId, origenId,
        tipologiaId, nivelId, tramite, documento, guia, causa, caja, falla,
        tecnicoCliente, prealertaId, cruce, novedad, garantiaFabricante,
        usuarioId, fecha, usuarioIdMovimiento, fechaMovimiento,
        observaciones, estadoCliente, loteId, cajaIngresoId, modeloId, SmartCard
    )
    SELECT
        UPPER(d.Serial),
        CASE WHEN ISNULL(d.Mac, '') IN ('', '0') THEN '0' ELSE UPPER(d.Mac) END,
        UPPER(ISNULL(d.Serial3, '')),
        UPPER(ISNULL(d.Serial4, '')), UPPER(ISNULL(d.Serial5, '')),
        d.CodigoSapId, d.PalletId, d.PalletId,
        d.EstadoId, d.TipoOrigenId, d.OrigenId,
        d.TipologiaId, d.NivelId, d.Tramite, d.Documento, d.Guia,
        d.Causa, 0, NULL, NULL, 0, 0, NULL, 0,
        d.UsuarioId, GETDATE(), d.UsuarioId, GETDATE(),
        NULL, d.EstadoCliente, d.LoteId, NULL, d.ModeloId, d.SmartCard
    FROM OPENJSON(@json) AS j
    CROSS APPLY OPENJSON(j.[value])
    WITH (
        Serial VARCHAR(50) '$.serial',
        Mac VARCHAR(50) '$.mac',
        Serial3 VARCHAR(50) '$.serial3',
        Serial4 VARCHAR(50) '$.serial4',
        Serial5 VARCHAR(50) '$.serial5',
        CodigoSapId INT '$.codigoSapId',
        PalletId INT '$.palletId',
        EstadoId INT '$.estadoId',
        TipoOrigenId INT '$.tipoOrigenId',
        OrigenId INT '$.origenId',
        TipologiaId INT '$.tipologiaId',
        NivelId INT '$.nivelId',
        Tramite VARCHAR(30) '$.tramite',
        Documento VARCHAR(30) '$.documento',
        Guia VARCHAR(30) '$.guia',
        Causa VARCHAR(50) '$.causa',
        UsuarioId INT '$.usuarioId',
        EstadoCliente VARCHAR(20) '$.estadoCliente',
        LoteId INT '$.loteId',
        ModeloId INT '$.modeloId',
        SmartCard VARCHAR(50) '$.smartCard'
    ) AS d
    WHERE NOT EXISTS (SELECT 1 FROM Ingreso i WHERE i.Serial = d.Serial)
    ORDER BY CONVERT(INT, j.[key]);

    SELECT @@ROWCOUNT AS Insertados;
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_ConfirmUnreadable]
@ConsecutivoSerial VARCHAR(50),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ilegible SET confirmado=1 
	WHERE consecutivoSerial=@ConsecutivoSerial
	select @Filas=@@ROWCOUNT
END




go


CREATE PROCEDURE [dbo].[pa_DeleteAccCodigoSap]
@codigoSapId int,
@accesorioId int
AS
BEGIN
	delete from CodigoSapAccesorio where codigoSapId=@codigoSapId and AccesorioId=@accesorioId

END
go


CREATE PROCEDURE [dbo].[pa_DeleteAccesory]
@PalletId int,
@Cantidad int,
@CodigoSapId int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	DELETE FROM Accesorio where id in(select top(@Cantidad)a.id from Accesorio a  
	where a.PalletId=@PalletId and a.CodigoSapId=@CodigoSapId)

END
go

CREATE PROCEDURE [dbo].[pa_DeleteAllComponentsAsig]
			@SerialId INT
			AS
			BEGIN

				SET NOCOUNT ON;
					DELETE FROM SerialComponente WHERE SerialId = @SerialId
			END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteAllPrealertSerial]
@PrealertaId int
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	delete from PrealertaSerial where prealertaId=@PrealertaId

END




go


CREATE   PROCEDURE [dbo].[pa_DeleteAssemble]
    @Serial VARCHAR(50)
AS
BEGIN
    --SET NOCOUNT ON;

    DELETE FROM Ensamble
    WHERE Id = (SELECT max(e.id) 
                FROM Ensamble e
				INNER JOIN Ingreso i ON e.serialId=i.id 
                WHERE i.Serial = @Serial);
END
go


CREATE PROCEDURE [dbo].[pa_DeleteBase]
@Base VARCHAR(50)
AS
/*
DECLARE @Base VARCHAR(50)
SET @Base =''
EXEC pa_DeleteBase @Base
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    IF(@Base='BaseDespacho')
	BEGIN
		DELETE FROM dbo.BaseDespacho
	END
	ELSE
	IF(@Base='BaseEmpaque')
	BEGIN
		DELETE FROM dbo.BaseEmpaque
	END
	ELSE
	IF(@Base='BaseInventario')
	BEGIN
		DELETE FROM dbo.BaseInventario
	END
	ELSE
	IF(@Base='BaseSeparacion')
	BEGIN
		DELETE FROM dbo.BaseSeparacion
	END
	ELSE
	IF(@Base='BaseIngreso')
	BEGIN
		DELETE FROM dbo.BaseIngreso
	END
    IF(@Base='BaseEtiquetado')
	BEGIN
		DELETE FROM dbo.BaseEtiquetado
	END

END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteBoxDispatch]
@CajaDespachoId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_DeleteBoxDispatch @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	DELETE FROM CajaDespacho 
	WHERE Id=@CajaDespachoId

END




go

CREATE PROCEDURE [dbo].[pa_DeleteBoxEntry]
    @CajaIngresoId INT
    AS
    BEGIN
        
        DELETE FROM CajaIngreso
        WHERE Id=@CajaIngresoId

    END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteBoxPacking]
@CajaEmpaqueId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_DeleteBoxPacking @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	DELETE FROM CajaEmpaque 
	WHERE Id=@CajaEmpaqueId

END




go

CREATE PROCEDURE [dbo].[pa_DeleteByPallet]
	@PalletId int
AS
/*
DECLARE @PalletId int
SET @PalletId = 9
EXEC pa_DeleteByPallet @PalletId
*/
BEGIN
	DELETE FROM Ingreso WHERE PalletId = @PalletId
	DELETE FROM Pallet WHERE Id = @PalletId
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteCiclic]
@CiclicoId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_DeleteCiclic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	DELETE FROM Ciclico 
	WHERE Id=@CiclicoId

END




go

CREATE PROCEDURE [dbo].[pa_DeleteCiudad]
            @Id int
            AS

            BEGIN
                -- SET NOCOUNT ON added to prevent extra result sets from
                -- interfering with SELECT statements.

                DELETE FROM Ciudad 
                WHERE Id=@Id
            END
go

CREATE   PROCEDURE [dbo].[pa_DeleteClasificacion]
@Serial VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	DELETE FROM Clasificacion
	WHERE id=(SELECT max(id)id FROM Clasificacion 
	WHERE serial=@Serial)

END
----------------------------------------------------------------------------------------------------------
go


CREATE   PROCEDURE [dbo].[pa_DeleteCleaning]
    @Serial VARCHAR(50)
AS
BEGIN
    DELETE FROM Limpieza
    WHERE Id = (SELECT MAX(c.id) 
                FROM Limpieza c
                INNER JOIN Ingreso i ON c.serialId = i.id 
                WHERE i.Serial = @Serial);
END;
go

 
CREATE   PROCEDURE [dbo].[pa_DeleteCodigoSapFalla]
    @codigoSapId INT,
    @fallaId INT
AS
BEGIN
    SET NOCOUNT ON;   
    DELETE FROM CodigoSapFalla
    WHERE CodigoSapId = @codigoSapId AND FallaId = @fallaId;        
END
go

CREATE PROCEDURE [dbo].[pa_DeleteComponentsAsig]
    @Serial VARCHAR(50),
    @ComponenteId INT
    AS
    BEGIN
        DELETE FROM SerialComponente 
        WHERE SerialId = (SELECT id FROM Ingreso WHERE Serial = @Serial)
        AND ComponenteId =  @ComponenteId
    END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteConfirmUnreadable]
@SerialConfirmacion VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ilegible SET confirmado=0 
	WHERE serialConfirmacion=@SerialConfirmacion

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteDiagnostic]
@Serial VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	DELETE FROM Diagnostico 
	WHERE id=(SELECT max(id)id FROM Diagnostico 
	WHERE serial=@Serial)

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteDispatch]
@Serial VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	DELETE FROM Despacho 
	WHERE serialId in(SELECT id FROM Ingreso 
	WHERE serial=@Serial)

END




go


CREATE   PROCEDURE dbo.pa_DeleteEntries
    @SerialesJson NVARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    DELETE i
    FROM Ingreso i
    INNER JOIN OPENJSON(@SerialesJson) WITH (Serial VARCHAR(50) '$') s
        ON i.Serial = s.Serial;
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteEntry]
@Serial varchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	DELETE FROM Ingreso where serial=@Serial

END



go

CREATE PROCEDURE [dbo].[pa_DeleteForecast] @id INT AS BEGIN SET NOCOUNT ON; DELETE FROM Forecast WHERE id = @id; END;
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteLabel]
@Id INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	DELETE FROM Etiqueta 
	WHERE Id=@Id

END



go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteLabelField]
@Id int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	delete from EtiquetaCampo where id=@Id
END



go


    CREATE PROCEDURE [dbo].[pa_DeleteLabourUser]
        @ManodeObraId INT,
        @UsuarioId INT
        AS
        BEGIN
            DELETE FROM ManoDeObraUsuario WHERE manoDeObraId = @ManoDeObraId AND usuarioId=@UsuarioID
        END
go

CREATE PROCEDURE [dbo].[pa_DeleteLote]
            @Id int
            AS

            BEGIN
                -- SET NOCOUNT ON added to prevent extra result sets from
                -- interfering with SELECT statements.

                DELETE FROM Lote 
                WHERE Id=@Id
            END
go

CREATE PROCEDURE [dbo].[pa_DeleteManoDeObra] @Id INT AS BEGIN SET NOCOUNT ON; DELETE FROM ManoDeObra WHERE Id = @Id; END;
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteMaster]
@Id int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	DELETE FROM Maestro 
	WHERE Id=@Id

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteMasterType]
@TipoMaestroId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	delete from TipoMaestro where id=@TipoMaestroId

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeletePacking]
@Serial VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	DELETE FROM Empaque 
	WHERE serialId in(SELECT id FROM Ingreso 
	WHERE serial=@Serial)

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeletePallet]
@PalletId int
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    delete from Pallet 
	where id=@PalletId

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeletePart]
@ParteId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	delete from Parte where id=@ParteId

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeletePrealert]
@Id int
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    delete from Prealerta where Id=@Id

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeletePrealertSerial]
@PrealertaId int,
@Id int
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	delete from PrealertaSerial where prealertaId=@PrealertaId and id=@Id

END




go

CREATE PROCEDURE [dbo].[pa_DeletePresupuesto]
            @Id int
            AS

            BEGIN

                DELETE FROM Presupuesto 
                WHERE Id=@Id
            END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteQuality]
@Serial VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_DeleteQuality @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	DELETE FROM Calidad 
	WHERE id=(SELECT max(c.id)id FROM Calidad c 
	INNER JOIN Ingreso i ON c.serialId=i.id 
	WHERE i.serial=@Serial)

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteRepair]
@Serial varchar(50)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	delete from Reparacion where id=(select max(id)id from Reparacion where serial=@Serial)

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteSapCode]
@Id INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_DeleteSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	DELETE FROM CodigoSap 
	WHERE Id=@Id

END




go


    CREATE PROCEDURE [dbo].[pa_DeleteSapCodeFailureComponent]
        @CodigoSapId INT,
        @FallaId INT,
        @ComponenteId INT
        AS
        BEGIN
            DELETE FROM codigoSapFallaComponente WHERE CodigoSapFallaId = (SELECT id  FROM codigoSapFalla where codigoSapId = @CodigoSapId AND fallaId = @FallaId )AND componenteId=@ComponenteId
        END
go

CREATE PROCEDURE [dbo].[pa_DeleteSmartCard]
        @Serial VARCHAR(50)
        AS
        BEGIN
            
            DELETE FROM SmartCard
            WHERE id=(SELECT max(id)id FROM SmartCard
            WHERE serial=@Serial)

        END
go

CREATE PROCEDURE [dbo].[pa_DeleteTransportadora]
            @Id int
            AS

            BEGIN
                -- SET NOCOUNT ON added to prevent extra result sets from
                -- interfering with SELECT statements.

                DELETE FROM Transportadora 
                WHERE Id=@Id
            END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteUnreadable]
@ConsecutivoSerial VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	DELETE FROM Ilegible 
	WHERE consecutivoSerial=@ConsecutivoSerial

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteWorflow]
@UsuarioId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	delete from WorkFlow where id=@UsuarioId

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_DeleteWorflowDestiny]
@WorkflowId int,
@DestinoId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	delete from WorkFlowDestino where workFlowId=@WorkflowId and destinoId=@DestinoId

END




go


          CREATE   PROCEDURE [dbo].[pa_EnviarPallet]
              @PalletId     INT,
              @DestinoId    INT,
              @EstadoId     INT,
              @TipologiaId  INT,
              @PosicionId   INT,
              @Estado       INT,
              @UsuarioId    INT,
              @Opcion       INT,
              @FilasIngreso INT OUTPUT,
              @FilasPallet  INT OUTPUT
          AS
          BEGIN
              SET NOCOUNT ON;

              DECLARE @BatchSize     INT = 500;
              DECLARE @TotalRows     INT;
              DECLARE @ProcessedRows INT = 0;
              DECLARE @EsMultimodelo BIT = 0;
              DECLARE @NumeroPallet  NVARCHAR(100);
              DECLARE @ErrMsg        NVARCHAR(400);
              DECLARE @ErrLine       INT;

              SET @FilasIngreso = 0;
              SET @FilasPallet  = 0;

              SELECT    
                  @NumeroPallet  = p.Numero,
                  @EsMultimodelo = CASE WHEN cs.Codigo = 'MULTIMODELO' THEN 1 ELSE 0 END
              FROM Pallet p
              INNER JOIN CodigoSap cs ON p.CodigoSapId = cs.Id
              WHERE p.Id = @PalletId;

              IF @NumeroPallet IS NULL
              BEGIN
                  RAISERROR('[pa_EnviarPallet] Pallet no encontrado. PalletId=%d | UsuarioId=%d', 16, 1, @PalletId, @UsuarioId);
                  RETURN;
              END

              IF @EsMultimodelo = 0
                  SET @TotalRows = (
                      SELECT COUNT(*) 
                      FROM Ingreso
                      WHERE PalletId = @PalletId
                        AND (estadoId <> @EstadoId OR tipologiaId <> @TipologiaId)
                  );
              ELSE
                  SET @TotalRows = (
                      SELECT COUNT(*) 
                      FROM Ingreso
                      WHERE PalletId = @PalletId
                        AND estadoId <> @EstadoId
                  );

              BEGIN TRY
                  BEGIN TRANSACTION;

                  WHILE @ProcessedRows < @TotalRows
                  BEGIN
                      IF @EsMultimodelo = 0
                      BEGIN
                          IF @Opcion = 0
                              UPDATE TOP (@BatchSize) Ingreso
                              SET estadoId            = @EstadoId,
                                  tipologiaId         = @TipologiaId,
                                  usuarioIdMovimiento = @UsuarioId,
                                  fechaMovimiento     = GETDATE()
                              WHERE PalletId = @PalletId
                                AND (estadoId <> @EstadoId OR tipologiaId <> @TipologiaId);
                          ELSE
                              UPDATE TOP (@BatchSize) Ingreso
                              SET estadoId            = @EstadoId,
                                  tipologiaId         = @TipologiaId,
                                  usuarioIdMovimiento = @UsuarioId,
                                  fechaMovimiento     = GETDATE(),
                                  palletId            = 0
                              WHERE PalletId = @PalletId
                                AND (estadoId <> @EstadoId OR tipologiaId <> @TipologiaId);
                      END
                      ELSE
                      BEGIN
                          IF @Opcion = 0
                              UPDATE TOP (@BatchSize) Ingreso
                              SET estadoId            = @EstadoId,
                                  usuarioIdMovimiento = @UsuarioId,
                                  fechaMovimiento     = GETDATE()
                              WHERE PalletId = @PalletId
                                AND estadoId <> @EstadoId;
                          ELSE
                              UPDATE TOP (@BatchSize) Ingreso
                              SET estadoId            = @EstadoId,
                                  usuarioIdMovimiento = @UsuarioId,
                                  fechaMovimiento     = GETDATE(),
                                  palletId            = 0
                              WHERE PalletId = @PalletId
                                AND estadoId <> @EstadoId;
                      END

                      IF @@ROWCOUNT = 0
                          BREAK;

                      SET @FilasIngreso  = @FilasIngreso + @@ROWCOUNT;
                      SET @ProcessedRows = @ProcessedRows + @@ROWCOUNT;

                      WAITFOR DELAY '00:00:00.100';
                  END

                  UPDATE Pallet
                  SET destinoId   = @DestinoId,
                      tipologiaId = @TipologiaId,
                      posicionId  = @PosicionId,
                      activo      = @Estado
                  WHERE Id = @PalletId;

                  SET @FilasPallet = @@ROWCOUNT;

                  IF @FilasPallet = 0
                  BEGIN
                      ROLLBACK TRANSACTION;
                      RAISERROR('[pa_EnviarPallet] El pallet no fue actualizado. PalletId=%d | Pallet=%s | DestinoId=%d | UsuarioId=%d', 16, 1, @PalletId, @NumeroPallet, @DestinoId, @UsuarioId);
                      RETURN;
                  END

                  COMMIT TRANSACTION;
              END TRY
              BEGIN CATCH
                  IF XACT_STATE() <> 0
                      ROLLBACK TRANSACTION;

                  SET @ErrMsg  = LEFT(ERROR_MESSAGE(), 400);
                  SET @ErrLine = ERROR_LINE();

                  RAISERROR('[pa_EnviarPallet] Error en transaccion. PalletId=%d | Pallet=%s | DestinoId=%d | UsuarioId=%d | Linea=%d | Detalle: %s', 16, 1, @PalletId, @NumeroPallet, @DestinoId, @UsuarioId, @ErrLine, @ErrMsg);
              END CATCH
          END
go


CREATE PROCEDURE [dbo].[pa_GetAlertByCustomer]
	@ClienteId int,
	@AlertId int
AS
/*
DECLARE @ClienteId int
SET @ClienteId = 2
DECLARE @AlertId int
SET @AlertId = 65
EXEC pa_GetAlertByCustomer @ClienteId, @AlertId
*/
BEGIN

DECLARE @Cliente nvarchar(20)
DECLARE @Quantity int
DECLARE @Origen varchar(30)
DECLARE @Destino varchar(30)
DECLARE @DestinoId int

CREATE TABLE #tempEnds (Id int IDENTITY (1,1) NOT NULL, CustomerId int, Customer nvarchar(30), Position nvarchar(30), Date datetime, Number varchar(20),
Source nchar(30), Destiny nchar(30), Typology nchar(30), CodeSap varchar(20), Model nvarchar(40), Card nvarchar(20), UserName nvarchar(20), Quantity int)

SELECT 
	TOP 1 p.id,p.Nombre as numero,ps.CodigoSap as codigo, ps.Descripcion as descripcion,'' as tipologia,o.Codigo as origen,'' as destino,u.nombreusuario,p.fecha,'' as posicion, p.Guia as Tarjeta
INTO #temp
FROM Prealerta p
INNER JOIN PrealertaSerial ps ON p.Id = ps.PrealertaId 
--INNER JOIN Maestro t on p.TipologiaId=t.Id 
INNER JOIN Maestro o on p.origenId=o.id 
--INNER JOIN WmsWdGeneral.dbo.Estado d on p.destinoId=d.id 
INNER JOIN WmsWdGeneral.dbo.Usuario u on p.usuarioId=u.id 
WHERE p.id = @AlertId

SELECT @Cliente = Nombre FROM WmsWdGeneral.dbo.Cliente WHERE Id = @ClienteId
SELECT @Origen = origen FROM #temp

SELECT @Quantity = ISNULL(COUNT(Cantidad),0) FROM PrealertaSerial WHERE PrealertaId = @AlertId

SELECT 
	CodigoSap, Count(*) as Cant
INTO #tempC
FROM Prealerta p
INNER JOIN PrealertaSerial ps ON p.Id = ps.PrealertaId
WHERE p.id = @AlertId
GROUP BY CodigoSap

DECLARE @CantCode int

SELECT @CantCode = count(*) FROM #tempC

IF(@CantCode=1)
BEGIN
INSERT INTO #tempEnds (CustomerId, Customer, Position, Date, Number, Source, Destiny, Typology, CodeSap, Model, Card, UserName, Quantity)
SELECT @ClienteId, @Cliente, Posicion, Fecha, Numero, Origen, Destino, Tipologia, Codigo, Descripcion, Tarjeta, NombreUsuario, @Quantity FROM #temp
END
ELSE
BEGIN
INSERT INTO #tempEnds (CustomerId, Customer, Position, Date, Number, Source, Destiny, Typology, CodeSap, Model, Card, UserName, Quantity)
SELECT @ClienteId, @Cliente, Posicion, Fecha, Numero, Origen, Destino, Tipologia, 'MULTIMODELO', 'MULTIMODELO', Tarjeta, NombreUsuario, @Quantity FROM #temp
END

SELECT * FROM #tempEnds

DROP TABLE #temp
DROP TABLE #tempC
DROP TABLE #tempEnds

END




go


CREATE   PROCEDURE [dbo].[pa_GetAssembledUser]
    @Perfil VARCHAR(20),
    @UsuarioId INT
AS
BEGIN
    SET NOCOUNT ON;
   WITH Ultimos AS (
        SELECT e.Id,e.Serial,e.Mac,e.Serial3,e.Serial4,e.Serial5,cs.Codigo AS CodigoSap,cs.Descripcion,u2.Nombre_Usuario AS UsuarioAsignado,e.NivelId,
        e.LoteId,le.Nombre as Lote,ml.codigo as Modelo,e.Fecha,ROW_NUMBER() OVER (PARTITION BY e.SerialId ORDER BY e.Id DESC) AS rn
        FROM Ensamble e
        INNER JOIN Ingreso i ON e.SerialId = i.Id
        INNER JOIN Estado es ON i.EstadoId = es.Id
        INNER JOIN CodigoSap cs ON e.CodigoSapId = cs.Id
        INNER JOIN UsuarioSys u ON e.UsuarioId = u.Id
		LEFT JOIN Maestro ml ON ml.Id =i.modeloid
		LEFT JOIN lote le ON e.loteid = le.Id
        LEFT JOIN UsuarioSys u2 ON e.UsuarioIdAsignado = u2.Id
        WHERE es.Nombre = 'ENSAMBLE-PREASIGNADO'
          AND (@Perfil = 'ADMINISTRADOR' OR e.UsuarioId = @UsuarioId))
    SELECT * FROM Ultimos
    WHERE rn = 1
    ORDER BY Id DESC;
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetAssignedTechnicianRepair]
@Serial varchar(50)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	SET NOCOUNT ON;

	select t.nombreUsuario as tecnicoAsignacion from Reparacion r 
	inner join Maestro e on r.estadoFinalId=e.id 
	inner join WmsWdGeneral.dbo.usuario t on r.tecnicoAsignacionId=t.id 
	where r.id=(select max(id)id from Reparacion 
	where serial=@Serial)

END




go

CREATE PROCEDURE [dbo].[pa_GetBatchList]
        AS
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.

            SET NOCOUNT ON;

            SELECT nombre FROM Lote 
            WHERE activo = 1
            ORDER BY nombre
        END
go

CREATE PROCEDURE [dbo].[pa_GetBatchName]
        @Id INT
        AS
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.

            SET NOCOUNT ON;

            SELECT nombre FROM Lote
            WHERE id = @Id 
        END
go


CREATE PROCEDURE [dbo].[pa_GetBoxPallet]
@Id int,
@Estado varchar(50)
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    IF(@Estado='Empaque')
	BEGIN
		select count(ce.id)cajas from CajaEmpaque ce 
		full outer join Pallet p on ce.palletId=p.id 
		where p.id=@Id
	END
	ELSE IF (@Estado = 'Despacho')
	BEGIN
		select count(cd.id)cajas from CajaDespacho cd 
		full outer join Pallet p on cd.palletId=p.id 
		where p.id=@Id and cd.Activo=1
	END
    ELSE IF(@Estado = 'Ingreso')
    BEGIN
		select count(ci.id)cajas from CajaIngreso ci 
		full outer join Pallet p on ci.palletId=p.id 
		where p.id=@Id and ci.Activo=1
	END

END
go


CREATE   PROCEDURE [dbo].[pa_GetClassifiedUser]
    @Perfil VARCHAR(20),
    @UsuarioId INT
AS
BEGIN
    SET NOCOUNT ON;
   WITH Ultimos AS (
        SELECT c.Id,c.SerialId,c.Serial,c.Mac,cs.Codigo AS CodigoSap,cs.Descripcion,u2.Nombre_Usuario AS UsuarioAsignado,c.NivelId,
        c.LoteId,le.Nombre as Lote,ml.codigo as Modelo,c.Fecha,ROW_NUMBER() OVER (PARTITION BY c.SerialId ORDER BY c.Id DESC) AS rn
        FROM Clasificacion c
        INNER JOIN Ingreso i ON c.SerialId = i.Id
        INNER JOIN Estado e ON i.EstadoId = e.Id
        INNER JOIN CodigoSap cs ON c.CodigoSapId = cs.Id
        INNER JOIN UsuarioSys u ON c.UsuarioId = u.Id
		LEFT JOIN Maestro ml ON ml.Id =i.modeloid
		LEFT JOIN lote le ON c.loteid = le.Id
        LEFT JOIN UsuarioSys u2 ON c.UsuarioAsignadoId = u2.Id
        WHERE e.Nombre = 'CLASIFICACION-PREASIGNADO'
          AND (@Perfil = 'ADMINISTRADOR' OR c.UsuarioId = @UsuarioId))
    SELECT * FROM Ultimos
    WHERE rn = 1
    ORDER BY Id DESC;
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetConsecutiveUnreadable]
@Cliente VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	SELECT consecutivoSerial FROM Ilegible 
	WHERE consecutivoSerial like 'ILE-%' and id=(SELECT max(id) FROM Ilegible)

END




go


CREATE PROCEDURE [dbo].[pa_GetCountBase]
@Base VARCHAR(50)
AS
/*
DECLARE @Base VARCHAR(50)
SET @Base=''
EXEC pa_GetCountBase @Base
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Base='BaseDespacho')
	BEGIN
		SELECT count(id)cantidad FROM dbo.BaseDespacho
	END
	ELSE
	IF(@Base='BaseEmpaque')
	BEGIN
		SELECT count(id)cantidad FROM dbo.BaseEmpaque
	END
	ELSE
	IF(@Base='BaseInventario')
	BEGIN
		SELECT count(id)cantidad FROM dbo.BaseInventario
	END
	ELSE
	IF(@Base='BaseSeparacion')
	BEGIN
		SELECT count(id)cantidad FROM dbo.BaseSeparacion
	END
	ELSE
	IF(@Base='BaseIngreso')
	BEGIN
		SELECT count(id)cantidad FROM dbo.BaseIngreso
	END
    ELSE
	IF(@Base='BaseEtiquetado')
	BEGIN
		SELECT count(id)cantidad FROM dbo.BaseEtiquetado
	END

END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetCountBoxDispatch]
@CajaDespachoId INT
AS
/*
DECLARE @CajaDespachoId INT
SET @CajaDespachoId = 0
EXEC pa_GetCountBoxDispatch @CajaDespachoId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    SELECT count(i.serial)cantidad FROM Ingreso i 
	INNER JOIN CajaDespacho cd ON i.cajaDespachoId=cd.id 
	WHERE cd.id=@CajaDespachoId

END




go

CREATE PROCEDURE [dbo].[pa_GetCountBoxEntry]
        @CajaIngresoId INT
        AS
        BEGIN
            SET NOCOUNT ON;

            SELECT count(i.serial)cantidad FROM Ingreso i 
            INNER JOIN CajaIngreso ci ON i.cajaIngresoId=ci.id 
            WHERE ci.id=@CajaIngresoId
        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetCountBoxPacking]
@CajaEmpaqueId INT
AS
/*
DECLARE @CajaEmpaqueId INT
SET @CajaEmpaqueId=0
EXEC pa_GetCountBoxPacking @CajaEmpaqueId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    SELECT count(i.serial)cantidad 
    FROM Ingreso i 
	INNER JOIN CajaEmpaque ce ON i.cajaEmpaqueId=ce.id 
	WHERE ce.id=@CajaEmpaqueId

END
go

CREATE   PROCEDURE [dbo].[pa_GetCountDiff]
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    SELECT
    (SELECT COUNT(id) FROM Ingreso) - (SELECT COUNT(id) FROM Inventario WHERE sobrante = 0) AS Faltante;
END
go



CREATE   PROCEDURE [dbo].[pa_GetCountDiffSap]
    @CodigoSapId INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        (SELECT COUNT(Id) FROM Ingreso WHERE CodigoSapId = @CodigoSapId) -
        (SELECT COUNT(inv.Id)
         FROM Inventario inv
         INNER JOIN CodigoSap cs ON cs.Codigo = inv.CodigoSap
         WHERE inv.Sobrante = 1 AND cs.Id = @CodigoSapId) AS Faltante;
END
go

 
CREATE   PROCEDURE [dbo].[pa_GetCountEntry]
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
 
    SET NOCOUNT ON;
 
    SELECT COUNT(id) FROM Ingreso;
 
END
go

CREATE   PROCEDURE [dbo].[pa_GetCountEntrySap]
    @CodigoSapId INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT COUNT(Id) AS Conteo
    FROM Ingreso
    WHERE CodigoSapId = @CodigoSapId;
END
go


CREATE   PROCEDURE [dbo].[pa_GetCountInventory]
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    SELECT COUNT(Id) AS Conteo
    FROM Inventario;
END
go



CREATE   PROCEDURE [dbo].[pa_GetCountInventorySap]
    @CodigoSapId INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT COUNT(inv.Id) AS Conteo
    FROM Inventario inv
    INNER JOIN CodigoSap cs ON cs.Codigo = inv.CodigoSap
    WHERE cs.Id = @CodigoSapId;
END
go


    CREATE PROCEDURE [dbo].[pa_GetCountLabour]
        @Tipo varchar(10),
        @ManoDeObraId INT
        AS
        BEGIN
            IF(@Tipo = 'UsuarioActivo')
                BEGIN    
                    select COUNT(id) contador
                    FROM ManoDeObraUsuario
                    WHERE usuarioActivo = 1 AND  ManoDeObraId = @ManoDeObraId     
                END
            ELSE IF (@Tipo = 'Altas')
                BEGIN
                    select COUNT(id) contador
                    FROM ManoDeObraUsuario
                    WHERE altas = 1 AND  ManoDeObraId = @ManoDeObraId 
                END
            ELSE IF (@Tipo = 'Bajas')
                BEGIN
                    select COUNT(id) contador
                    FROM ManoDeObraUsuario
                    WHERE bajas = 1 AND  ManoDeObraId = @ManoDeObraId 
                END
        END
go



CREATE   PROCEDURE [dbo].[pa_GetCountMasterPrintingByBox]
    @CajaEmpaqueId INT,
    @TotalCount INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT @TotalCount = COUNT(*) 
    FROM MasterImpresas 
    WHERE CajaEmpaqueId = @CajaEmpaqueId;
END;
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetCountMasterType]
@TipoMaestroId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	set nocount on;

	select count(id)cantidad from Maestro where tipoMaestroId=@TipoMaestroId

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- ALTER date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetCountPallet]
@Id int,
@Tabla varchar(20)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	IF(@Tabla='INGRESO')
	BEGIN
		SELECT COUNT(serial)cantidad FROM dbo.Ingreso 
		WHERE palletId=@Id
	END
	ELSE
	IF(@Tabla='ACCESORIO')
	BEGIN
		SELECT COUNT(id)cantidad FROM dbo.Accesorio 
		WHERE palletId=@Id
	END    
	ELSE
	IF(@Tabla='PARTES')
	BEGIN
		SELECT COUNT(id)cantidad FROM dbo.Parte 
		WHERE palletId=@Id
	END 
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetCountSapCode]
@CodigoId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    SELECT count(cs.id)cantidad FROM Ingreso i 
	INNER JOIN CodigoSap cs ON i.CodigoSapId=cs.Id 
	INNER JOIN Despacho d ON cs.Id=d.CodigoSapId 
	WHERE cs.id=@CodigoId

END




go


CREATE PROCEDURE [dbo].[pa_GetCountState]
		@Estado varchar(20)
		AS
		BEGIN

			SET NOCOUNT ON;

			SELECT COUNT(i.serial)cantidad FROM dbo.Ingreso i
			INNER JOIN Estado e ON i.estadoId = e.Id 
			WHERE e.Nombre IN (SELECT Nombre FROM Estado
                                WHERE PadreInventario = @Estado) 

		END
go

CREATE PROCEDURE [dbo].[pa_GetCountSurplus]
        AS
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.
        
            SET NOCOUNT ON;
        
            SELECT COUNT(Serial) FROM Inventario
            WHERE Sobrante = 1;        
        END
go


CREATE PROCEDURE [dbo].[pa_GetDayWarranty]
	@SerialId int
as
/*
declare @SerialId int
set @SerialId = 126880
exec pa_GetDayWarranty @SerialId
*/
begin
Declare @FDespacho datetime

SELECT @FDespacho = Fecha from Despacho where SerialId = @SerialId

DECLARE @enddate   datetime2 = getdate();   

SELECT DATEDIFF(day, @FDespacho, @enddate) as dias; 

end




go


CREATE PROCEDURE [dbo].[pa_GetDescriptionMaster]
    @Codigo varchar(50)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT descripcion
    FROM Maestro
    WHERE codigo = @Codigo
      AND activo = 1;
END;
go


CREATE   PROCEDURE [dbo].[pa_GetDiagnosedUser]
    @Perfil VARCHAR(50),
    @UsuarioId INT
AS
BEGIN
    SET NOCOUNT ON;
   WITH Ultimos AS (
        SELECT
            dg.Id, dg.SerialId, dg.Serial, dg.Mac, cs.Codigo AS CodigoSap, cs.Descripcion,
            0 AS Fallaid, 0 AS EstadoFinalid, u.Nombre_Usuario AS Usuario,
            i.LoteId, le.Nombre AS Lote,
            CASE WHEN EXISTS (SELECT 1 FROM PQRS q WHERE q.SerialId = dg.SerialId) THEN 'SI' ELSE 'NO' END AS PQRS,
            dg.Fecha,
            ROW_NUMBER() OVER (PARTITION BY dg.SerialId ORDER BY dg.Id DESC) AS rn
        FROM Diagnostico dg
        INNER JOIN Ingreso i ON dg.SerialId = i.Id
        INNER JOIN Estado e ON i.EstadoId = e.Id
        INNER JOIN CodigoSap cs ON dg.CodigoSapId = cs.Id
        INNER JOIN UsuarioSys u ON dg.UsuarioId = u.Id
        LEFT JOIN Lote le ON i.LoteId = le.Id
        WHERE e.Nombre = 'DIAGNOSTICO-PREASIGNADO'
          AND (@Perfil = 'ADMINISTRADOR' OR i.UsuarioIdMovimiento = @UsuarioId)
    )
    SELECT DG.Id, DG.SerialId, DG.Serial, DG.Mac, DG.CodigoSap, DG.Descripcion,
        DG.Fallaid, DG.EstadoFinalid, DG.Usuario, DG.LoteId, DG.Lote, DG.PQRS, DG.Fecha
    FROM Ultimos DG
    WHERE DG.rn = 1
    ORDER BY DG.Id DESC;
END
go


CREATE   PROCEDURE [dbo].[pa_GetDiagnosticVariables]
@Serial varchar(50)
AS
BEGIN
	SELECT d.variable1, d.variable2, d.variable3, d.variable4 FROM Diagnostico d WHERE d.Serial = @serial	
END
go

CREATE   PROCEDURE [dbo].[pa_GetDiffReport]
AS
BEGIN      
    SET NOCOUNT ON;
 
    SELECT cs.codigo AS CodigoSap,
           cs.Descripcion,
           p.numero AS Pallet,
           i.Serial AS Principal,
           i.mac AS Secundario,
           i.Caja,
           tip.codigo AS Estado,
           loc.nombre AS Localizacion,
           f.codigo AS Familia,
           c.Codigo AS Tipologia,
           po.numero AS Posicion,
           i.Fecha,
           i.FechaMovimiento,
           tor.codigo AS TipoOrigen,
           o.codigo AS Origen,
           op.nombre AS OrigenPallet
    FROM Ingreso I
    LEFT JOIN codigosap cs ON i.codigosapid = cs.id
    LEFT JOIN pallet p ON i.palletid = p.id
    LEFT JOIN estado loc ON i.estadoid = loc.id
    LEFT JOIN maestro tip ON i.tipologiaid = tip.id
    LEFT JOIN Maestro f ON cs.FamiliaId=f.Id
    LEFT JOIN Maestro c ON cs.ClasificacionId=c.id
    LEFT JOIN Posicion po ON p.PosicionId=po.id
    LEFT JOIN Maestro tor ON i.TipoOrigenId=tor.id
    LEFT JOIN Maestro o ON i.OrigenId=o.id
    LEFT JOIN Estado op ON p.OrigenId=op.id
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetDifferencePrealert]
@Id int
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	select count(serial)total from Ingreso where prealertaId=@Id and cruce=1

END




go

CREATE PROCEDURE [dbo].[pa_GetEntryProgress]
	@PalletId int,
	@EstadoId int
AS
BEGIN
	DECLARE @TotalCount INT;
	DECLARE @TotalRemaining INT;
	DECLARE @TotalProcessed INT;
	
	SELECT @TotalCount = COUNT(*) 
	FROM Ingreso WITH (NOLOCK)
	WHERE PalletId = @PalletId;
	
	SELECT @TotalRemaining = COUNT(*) 
	FROM Ingreso WITH (NOLOCK)
	WHERE PalletId = @PalletId AND estadoId <> @EstadoId;
	
	SET @TotalProcessed = @TotalCount - @TotalRemaining;
	
	SELECT 
		@TotalCount AS totalCount,
		@TotalProcessed AS totalProcessed,
		@TotalRemaining AS totalRemaining;
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetFailureDiagnostic]
@Serial VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;
	
	SELECT m.codigo AS falla,m.descripcion FROM Diagnostico d 
	INNER JOIN Maestro m ON d.fallaId=m.id 
	WHERE d.id=(SELECT max(id)id FROM Diagnostico 
	WHERE serial=@Serial)

END




go

CREATE   PROCEDURE [dbo].[pa_GetFailureIdDiagnostic]
@SerialId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
    SET NOCOUNT ON;

    SELECT TOP (1) ISNULL(fallaId, 0) AS FallaId
    FROM dbo.Diagnostico
    WHERE SerialId = @SerialId
    ORDER BY Id DESC;
END
---------------------------------------------------------------
go

CREATE PROCEDURE [dbo].[pa_GetFamilyAcronyms]
    @CodigoSap varchar(50),
    @Destino VARCHAR(50)

    AS
    BEGIN
        -- SET NOCOUNT ON added to prevent extra result sets from
        -- interfering with SELECT statements.
        
        set nocount on;
        IF(@Destino = 'INGRESO' OR @Destino = 'ALMACENAMIENTO')
            BEGIN
                SELECT m.Descripcion AS siglas FROM Maestro m                     
                WHERE m.Codigo = 'LECTURA'
            END
        ELSE
            BEGIN
                SELECT m.Descripcion AS siglas FROM Maestro m 
                INNER JOIN CodigoSap cs ON cs.FamiliaId = m.Id
                WHERE cs.Codigo = @CodigoSap
            END
    END
go


    CREATE PROCEDURE [dbo].[pa_GetFamilyId]
		@CodigoSap varchar(50)
		AS
		BEGIN
			-- SET NOCOUNT ON added to prevent extra result sets from
			-- interfering with SELECT statements.
			
			set nocount on;
			SELECT FamiliaId FROM CodigoSap WHERE Codigo = @CodigoSap
		END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetFamilyMaster]
@CodigoSap varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	SELECT m.codigo FROM Maestro m 
	INNER JOIN CodigoSap cs ON m.id=cs.familiaId 
	WHERE cs.codigo=@CodigoSap

END




go

CREATE PROCEDURE [dbo].[pa_GetFamilyNumberBox]
@CodigoSap varchar(50),
@Destino VARCHAR(50)
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    set nocount on;
    IF(@Destino = 'INGRESO' OR @Destino = 'ALMACENAMIENTO')
        BEGIN
            SELECT CONVERT(INT,m.adicional) AS scaja FROM Maestro m                                    
            WHERE m.Codigo = 'LECTURA'
        END
    ELSE
        BEGIN
            SELECT CONVERT(INT,m.adicional) AS scaja FROM Maestro m 
            INNER JOIN CodigoSap cs ON cs.FamiliaId = m.Id
            WHERE cs.Codigo = @CodigoSap
        END
END
go

/**
 * Author: Pedro Neira
 * Created: MAY, 7  2025
 */
CREATE PROCEDURE [dbo].[pa_GetFamilyNumberPallet]
    @CodigoSap varchar(50),
    @Destino VARCHAR(50)        
    AS
    BEGIN
        -- SET NOCOUNT ON added to prevent extra result sets from
        -- interfering with SELECT statements.
        
        set nocount on;

		IF(@Destino = 'INGRESO' OR @Destino = 'ALMACENAMIENTO')
			BEGIN
				SELECT CONVERT(INT,m.Detalle) AS spallet FROM Maestro m                     
				WHERE m.Codigo = 'LECTURA'
			END
        ELSE
            BEGIN
                SELECT CONVERT(INT,m.Detalle) AS spallet FROM Maestro m 
                INNER JOIN CodigoSap cs ON cs.FamiliaId = m.Id
                WHERE cs.Codigo = @CodigoSap
            END
    END
go


CREATE PROCEDURE [dbo].[pa_GetFirstAlertCount]
	@FirstAlertId int = 0
as
/*
declare @FirstAlertId int
set @FirstAlertId = 72
exec pa_GetFirstAlertCount @FirstAlertId
*/
begin
declare @Cantidad int
declare @Cruzados int
declare @Total int

select @Cantidad = COUNT(Serial) from PrealertaSerial where prealertaId=@FirstAlertId
select @Cruzados = COUNT(Serial) from Ingreso where prealertaId=@FirstAlertId and cruce=1
set @Total = @Cantidad - @Cruzados

select Id, Nombre, @Cantidad as Cantidad, @Cruzados as Cruzados, @Total as Total from Prealerta where Id=@FirstAlertId

end




go



CREATE PROCEDURE [dbo].[pa_GetHeadquarterSource]
	@HeadquarterId int,
	@TypeMastrerId int,
	@Description varchar(100)
AS
/*
DECLARE @HeadquarterId int
DECLARE @TypeMastrerId int
DECLARE @Description varchar(100)
SET @HeadquarterId = 5
SET @TypeMastrerId = 337
SET @Description = 'ANEXOS'
EXEC pa_GetHeadquarterSource @HeadquarterId, @TypeMastrerId, @Description
*/
BEGIN
SELECT m.* FROM Maestro m 
INNER JOIN SedeOrigen so ON m.Id = OrigenId
WHERE so.SedeId = @HeadquarterId AND Descripcion = @Description
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetHistoricRepair]
@Serial varchar(50)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	SET NOCOUNT ON;

	select r.Serial,r.Mac,cs.Codigo,cs.Descripcion,e.Codigo as estadoFinal,f.Codigo+' | '+f.Descripcion as falla,u.NombreUsuario as tecnicoReparacion,r.FechaReparacion from Reparacion r 
	inner join CodigoSap cs on r.CodigoSapId=cs.Id 
	inner join Maestro e on r.EstadoFinalId=e.id 
	inner join Maestro f on r.Falla1Id=f.id 
	inner join WmsWdGeneral.dbo.Usuario u on r.TecnicoReparacionId=u.id 
	where r.Serial=@Serial

END




go

CREATE PROCEDURE [dbo].[pa_GetIdBatch]
        @Nombre VARCHAR(50)
        AS
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.

            SET NOCOUNT ON;

            SELECT id FROM Lote
            WHERE nombre = @Nombre 
        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetIdEntry]
@Serial varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT id FROM Ingreso 
	WHERE serial=@Serial

END




go

/**
 * Author:  Pedro Neira 
 * Created: SEP, 19  2024
 */ 

CREATE PROCEDURE [dbo].[pa_GetIdLevelRepairMaster]
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	SELECT m.id FROM Maestro m 
    INNER JOIN TipoMaestro tm ON m.TipoMaestroId = tm.id 
    WHERE tm.Nombre = 'Niveles'
    AND codigo = (SELECT CONVERT(VARCHAR, MAX(CONVERT(INT, Codigo)) - 1)
                FROM Maestro 
                WHERE TipoMaestroId = tm.Id AND TRY_CONVERT(INT, Codigo) IS NOT NULL);
    
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetIdMaster]
@Codigo varchar(50),
@TipoMaestro varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;
	IF(@TipoMaestro='Niveles' AND @Codigo='0')
	BEGIN
		SELECT min(m.Id)as id FROM Maestro m 
		INNER JOIN TipoMaestro tm ON m.tipoMaestroId=tm.id 
		WHERE tm.nombre=@TipoMaestro
	END
	ELSE
	BEGIN
		SELECT m.id FROM Maestro m 
		INNER JOIN TipoMaestro tm ON m.tipoMaestroId=tm.id 
		WHERE m.codigo=@Codigo and tm.nombre=@TipoMaestro
	END
END



go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetIdMasterType]
@Nombre varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	set nocount on;

	select id from TipoMaestro where nombre=@Nombre

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetIdPallet]
@Numero varchar(20)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select id from Pallet 
	where numero=@Numero and activo = 1;

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetIdPrealert]
@Nombre varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	set nocount on;

	select Id from Prealerta where Nombre=@Nombre

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetIdSapCode]
@Codigo VARCHAR(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    SELECT id FROM CodigoSap 
	WHERE codigo=@Codigo

END




go

CREATE PROCEDURE [dbo].[pa_GetIdSapCodeByDescription]
        @Codigo VARCHAR(20),
        @Descripcion VARCHAR(120)
        AS
        /*
        DECLARE @PalletId INT
        SET @PalletId=0
        EXEC pa_GetModelSapCode @PalletId
        */
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.
            SET NOCOUNT ON;

            SELECT id FROM CodigoSap 
            WHERE codigo=@Codigo AND Descripcion = @Descripcion

        END
go


CREATE PROCEDURE [dbo].[pa_GetIdSapCodeByDescriptionType]
        @Codigo VARCHAR(20),
        @Descripcion VARCHAR(120),
        @Tipo int
        AS
        /*
        DECLARE @PalletId INT
        SET @PalletId=0
        EXEC pa_GetModelSapCode @PalletId
        */
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.
            SET NOCOUNT ON;

            SELECT id FROM CodigoSap 
            WHERE codigo=@Codigo AND Descripcion = @Descripcion

        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetIdWorflowDestiny]
@Opcion varchar(20),
@Origen varchar(50),
@Descripcion varchar(50),
@TipologiaId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	set nocount on;

	IF(@Opcion='NINGUNA')
	BEGIN
		select d.id from WorkFlowDestino wd 
		inner join WorkFlow w on wd.workFlowId=w.id 
		inner join WmsWdGeneral.dbo.estado d on wd.destinoId=d.id 
		inner join WmsWdGeneral.dbo.estado o on w.origenId=o.id 
		where o.nombre=@Origen and w.OpcionId=0 and w.tipologiaId=@TipologiaId
	END
	ELSE
	BEGIN
		select d.id from WorkFlowDestino wd 
		inner join WorkFlow w on wd.workFlowId=w.id 
		inner join WmsWdGeneral.dbo.estado d on wd.destinoId=d.id 
		inner join WmsWdGeneral.dbo.estado o on w.origenId=o.id 
		left join WmsWdGeneral.dbo.permiso op on w.opcionId=op.id 
		where o.nombre=@Origen and op.nombre=@Opcion and op.descripcion=@Descripcion and w.tipologiaId=@TipologiaId
	END

END




go

CREATE PROCEDURE [dbo].[pa_GetIncomeByUserSouDesNumber]
	@UserId int = 0,
	@Source varchar(50) = null,
	@Destiny varchar(50) = null,
	@Number varchar(20) = null
as
/*
declare @UserId int
declare @Source varchar(50)
declare @Destiny varchar(50)
declare @Number varchar(20)
set @UserId = 1028
set @Source = 'INGRESO'
set @Destiny = 'INGRESO'
--set @Number = '1'
exec pa_GetIncomeByUserSouDesNumber @UserId, @Source, @Destiny, @Number
*/
begin
if (@UserId = 0) 
    if (@Number is null)
		select p.Id, p.Numero, COUNT(i.Serial) as Cantidad, o.Nombre as Origen, cs.Id as CodigoSapId, cs.Codigo, p.Fecha
		from dbo.Pallet p
		full outer join dbo.Ingreso i on p.Id = i.PalletId
		inner join dbo.CodigoSap cs on p.CodigoSapId = cs.Id
		inner join dbo.Estado o on p.OrigenId = o.Id
		inner join dbo.Estado d on p.DestinoId = d.Id
		inner join dbo.Maestro m on cs.TipoId = m.Id
		where o.Nombre = @Source and d.Nombre = @Destiny and p.Activo=1 and m.Codigo= 'SERIALIZADOS'
		group by p.Id, p.Numero, o.Nombre, cs.Id, cs.Codigo, p.Fecha
		union
		select p.Id, p.Numero, COUNT(*) as Cantidad, o.Nombre as Origen, cs.Id as CodigoSapId, cs.Codigo, p.Fecha
		from dbo.Pallet p
		full outer join dbo.Accesorio i on p.Id = i.PalletId
		inner join dbo.CodigoSap cs on p.CodigoSapId = cs.Id
		inner join dbo.Estado o on p.OrigenId = o.Id
		inner join dbo.Estado d on p.DestinoId = d.Id
		inner join dbo.Maestro m on cs.TipoId = m.Id
		where o.Nombre = @Source and d.Nombre = @Destiny and p.Activo=1 and m.Codigo= 'ACCESORIOS'
		group by p.Id, p.Numero, o.Nombre, cs.Id, cs.Codigo, p.Fecha

    else
        select p.Id, p.Numero, COUNT(i.Serial) as Cantidad, o.Nombre as Origen, cs.Id as CodigoSapId, cs.Codigo, p.Fecha 
		from dbo.Pallet p
		full outer join Ingreso i on p.Id = i.PalletId
		inner join dbo.CodigoSap cs on p.CodigoSapId = cs.Id
		inner join dbo.Estado o on p.OrigenId = o.Id
		inner join dbo.Estado d on p.DestinoId = d.Id
		inner join dbo.Maestro m on cs.TipoId = m.Id
		where o.Nombre = @Source and d.Nombre = @Destiny and p.Numero = @Number and p.Activo = 1 and m.Codigo= 'SERIALIZADOS'
		group by p.Id, p.Numero, o.Nombre, cs.Id, cs.Codigo, p.Fecha
		union
		select p.Id, p.Numero, COUNT(*) as Cantidad, o.Nombre as Origen, cs.Id as CodigoSapId, cs.Codigo, p.Fecha 
		from dbo.Pallet p
		full outer join Accesorio i on p.Id = i.PalletId
		inner join dbo.CodigoSap cs on p.CodigoSapId = cs.Id
		inner join dbo.Estado o on p.OrigenId = o.Id
		inner join dbo.Estado d on p.DestinoId = d.Id
		inner join dbo.Maestro m on cs.TipoId = m.Id
		where o.Nombre = @Source and d.Nombre = @Destiny and p.Numero = @Number and p.Activo = 1 and m.Codigo= 'ACCESORIOS'
		group by p.Id, p.Numero, o.Nombre, cs.Id, cs.Codigo, p.Fecha
else
    if (@Number is null)
        select p.Id, p.Numero, COUNT(i.Serial) as Cantidad, o.Nombre as Origen, cs.Id as CodigoSapId, cs.Codigo, p.Fecha
		from dbo.Pallet p
		full outer join dbo.Ingreso i on p.Id = i.PalletId
		inner join dbo.CodigoSap cs on p.CodigoSapId = cs.Id
		inner join dbo.Estado o on p.OrigenId = o.Id
		inner join dbo.Estado d on p.DestinoId = d.Id
		inner join dbo.Maestro m on cs.TipoId = m.Id
		where o.Nombre = @Source and d.Nombre = @Destiny and p.UsuarioId = @UserId and p.Activo = 1 and m.Codigo= 'SERIALIZADOS'
		group by p.Id, p.Numero, o.Nombre, cs.Id, cs.Codigo, p.Fecha
		union
		select p.Id, p.Numero, COUNT(*) as Cantidad, o.Nombre as Origen, cs.Id as CodigoSapId, cs.Codigo, p.Fecha
		from dbo.Pallet p
		full outer join dbo.Accesorio i on p.Id = i.PalletId
		inner join dbo.CodigoSap cs on p.CodigoSapId = cs.Id
		inner join dbo.Estado o on p.OrigenId = o.Id
		inner join dbo.Estado d on p.DestinoId = d.Id
		inner join dbo.Maestro m on cs.TipoId = m.Id
		where o.Nombre = @Source and d.Nombre = @Destiny and p.UsuarioId = @UserId and p.Activo = 1 and m.Codigo= 'ACCESORIOS'
		group by p.Id, p.Numero, o.Nombre, cs.Id, cs.Codigo, p.Fecha
    else
        select p.Id, p.Numero, COUNT(i.Serial) as Cantidad, o.Nombre as Origen, cs.Id as CodigoSapId, cs.Codigo, p.Fecha
		from dbo.Pallet p
		full outer join dbo.Ingreso i on p.id = i.PalletId
		inner join dbo.CodigoSap cs on p.CodigoSapId = cs.Id
		inner join dbo.Estado o on p.OrigenId = o.Id
		inner join dbo.Estado d on p.DestinoId = d.Id
		inner join dbo.Maestro m on cs.TipoId = m.Id
		where o.Nombre = @Source and d.Nombre = @Destiny and p.Numero = @Number and p.UsuarioId = @UserId and p.activo = 1 and m.Codigo= 'SERIALIZADOS'
		group by p.Id, p.Numero, o.Nombre, cs.Id, cs.Codigo, p.Fecha
		union
		select p.Id, p.Numero, COUNT(*) as Cantidad, o.Nombre as Origen, cs.Id as CodigoSapId, cs.Codigo, p.Fecha
		from dbo.Pallet p
		full outer join dbo.Accesorio i on p.id = i.PalletId
		inner join dbo.CodigoSap cs on p.CodigoSapId = cs.Id
		inner join dbo.Estado o on p.OrigenId = o.Id
		inner join dbo.Estado d on p.DestinoId = d.Id
		inner join dbo.Maestro m on cs.TipoId = m.Id
		where o.Nombre = @Source and d.Nombre = @Destiny and p.Numero = @Number and p.UsuarioId = @UserId and p.activo = 1 and m.Codigo= 'ACCESORIOS'
		group by p.Id, p.Numero, o.Nombre, cs.Id, cs.Codigo, p.Fecha
end



go


CREATE PROCEDURE [dbo].[pa_GetLastBoxDispatch]
@PalletId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId = 0
EXEC pa_GetLastBoxDispatch @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    SELECT MAX(convert(int,Numero))Numero FROM CajaDespacho 
	WHERE palletId=@PalletId

END
go

CREATE PROCEDURE [dbo].[pa_GetLastBoxEntry]
        @PalletId INT
        AS
        BEGIN
            SET NOCOUNT ON;

            SELECT MAX(Numero)Numero FROM CajaIngreso 
            WHERE palletId=@PalletId

        END
go

/**
 * Author:  Julian Agamez
 * Created: AGO, 16  2024
 */ 
CREATE PROCEDURE [dbo].[pa_GetLastBoxPacking]
@PalletId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetLastBoxPackin @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
 
    SELECT 
	MAX(convert(int,Numero))Numero 
	FROM CajaEmpaque 
	WHERE palletId=@PalletId
END
go

CREATE   PROCEDURE dbo.pa_GetLastMovimient
@PalletId int
AS 
SELECT origenId FROM Movimiento 
WHERE id = (SELECT MAX(m.id) FROM Movimiento m 
			INNER JOIN Ingreso i ON m.serialId = i.id 
			WHERE i.PalletId = @PalletId 
			AND m.OrigenId <> m.DestinoId)
go


CREATE   PROCEDURE dbo.pa_GetLastMovimientAccesory
@PalletId int
AS 
SELECT origenId FROM MovimientoAccesorio
WHERE id = (SELECT MAX(m.id) FROM MovimientoAccesorio m 
			INNER JOIN Accesorio a ON m.accesorioId = a.id 
			WHERE a.PalletId = @PalletId
			AND m.OrigenId <> m.DestinoId)
			
go

CREATE   PROCEDURE dbo.pa_GetLastMovimientAux
@PalletId int
AS 
SELECT origenId FROM Movimiento 
WHERE id = (SELECT MAX(m.id) FROM Movimiento m 
			INNER JOIN Ingreso i ON m.serialId = i.id 
			WHERE i.PalletId = @PalletId 
			AND m.OrigenId <> m.DestinoId)
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetLastRepair]
@Serial varchar(50)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	SET NOCOUNT ON;

	select e.codigo as estadoFinal,t.nombreUsuario as tecnicoreparacion,r.fechaReparacion from Reparacion r 
	inner join Maestro e on r.estadoFinalId=e.id 
	inner join WmsWdGeneral.dbo.usuario t on r.tecnicoReparacionId=t.id 
	where r.id=(select max(r.id)id from Reparacion r 
	inner join Maestro e on r.estadoFinalId=e.id 
	inner join WmsWdGeneral.dbo.usuario t on r.tecnicoReparacionId=t.id 
	where r.serial=@Serial)

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetLevelEntry]
@Serial varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT m.codigo FROM Ingreso i 
	INNER JOIN Maestro m ON i.nivelId=m.id 
	WHERE i.serial=@Serial

END




go


    CREATE PROCEDURE [dbo].[pa_GetLevelIdWorflow]
    @Opcion varchar(20),
    @Origen varchar(50),
    @Descripcion varchar(50),
    @TipologiaId int
    AS
    BEGIN
        SET NOCOUNT ON;

        IF (@Opcion = 'NINGUNA')
        BEGIN
            SELECT w.nivelId 
            FROM WorkFlow w
            INNER JOIN WmsWdGeneral.dbo.estado o ON w.origenId = o.id 
            WHERE o.nombre = @Origen AND w.OpcionId = 0 AND w.tipologiaId = @TipologiaId;
        END
        ELSE
        BEGIN
            SELECT w.nivelId 
            FROM WorkFlow w
            INNER JOIN WmsWdGeneral.dbo.estado o ON w.origenId = o.id 
            LEFT JOIN WmsWdGeneral.dbo.permiso op ON w.opcionId = op.id 
            WHERE o.nombre = @Origen AND op.nombre = @Opcion AND op.descripcion = @Descripcion AND w.tipologiaId = @TipologiaId;
        END
    END
go


CREATE PROCEDURE [dbo].[pa_GetLevelsClasification]
AS
BEGIN
    SET NOCOUNT ON;

    SELECT m.codigo
    FROM Maestro m
    INNER JOIN TipoMaestro tm
        ON m.tipoMaestroId = tm.id
    WHERE tm.nombre = 'Niveles'
      AND m.codigo < '4'
      AND m.activo = 1
    ORDER BY m.codigo;
END;
go


CREATE PROCEDURE [dbo].[pa_GetListDescriptionSapCode]
AS
BEGIN
    SET NOCOUNT ON;

    SELECT codigo,
           descripcion
    FROM CodigoSap
    WHERE activo = 1
    ORDER BY codigo;
END;
go


CREATE PROCEDURE [dbo].[pa_GetListDescriptionSapCodeNoSerial]
    @Id INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT c.codigo,
           c.descripcion
    FROM CodigoSap c
    WHERE c.activo = 1
      AND (
            c.TipoId = @Id
         OR c.Codigo = 'MULTIMODELO'
      )
    ORDER BY codigo;
END;
go


CREATE PROCEDURE [dbo].[pa_GetListDescriptionSapCodeSerial]
    @Id INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT c.codigo,
           c.descripcion
    FROM CodigoSap c
    INNER JOIN Maestro tcs
        ON c.TipoId = tcs.Id
    WHERE c.activo = 1
      AND (
            c.TipoId = @Id
         OR tcs.Codigo = 'SMARTCARD'
      )
    ORDER BY codigo;
END;
go

CREATE   PROCEDURE [dbo].[pa_GetListFailuresCodigoSap]
    @CodigoSapId INT
AS
BEGIN
    SET NOCOUNT ON;
 
    SELECT DISTINCT m.id, m.codigo, m.descripcion,csf.ComponenteObligatorio
    FROM CodigoSap cs
    INNER JOIN CodigoSapFalla csf ON cs.Id = csf.CodigoSapId
    INNER JOIN Maestro m ON csf.FallaId = m.Id
    WHERE cs.Id = @CodigoSapId;
END
go


CREATE PROCEDURE [dbo].[pa_GetListFailuresCodigoSapNoId]
    @codigo VARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        m.Codigo AS codigo,
        m.Descripcion AS descripcion
    FROM CodigoSap cs
    INNER JOIN CodigoSapFalla csf
        ON cs.Id = csf.CodigoSapId
    INNER JOIN Maestro m
        ON csf.FallaId = m.Id
    WHERE cs.Codigo = @codigo
      AND m.activo = 1;
END;
go


CREATE PROCEDURE [dbo].[pa_GetListFailuresMaster]
    @Nombre varchar(50)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT m.codigo,
           m.descripcion
    FROM Maestro m
    INNER JOIN TipoMaestro tm
        ON m.tipoMaestroId = tm.id
    WHERE tm.nombre = @Nombre
      AND m.activo = 1;
END;
go


CREATE PROCEDURE [dbo].[pa_GetListLabel]
    @Tipo VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT nombre
    FROM Etiqueta
    WHERE tipo = @Tipo
      AND activo = 1;
END;
go


-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetListLabelField]
@EtiquetaId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    SELECT nombre,valor from EtiquetaCampo where EtiquetaId=@EtiquetaId

END




go


CREATE PROCEDURE [dbo].[pa_GetListMaster]
    @TipoMaestro varchar(50)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT m.codigo
    FROM Maestro m
    INNER JOIN TipoMaestro tm
        ON m.tipoMaestroId = tm.id
    WHERE tm.nombre = @TipoMaestro
      AND m.activo = 1
    ORDER BY m.codigo;
END;
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetListMasterType]
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	set nocount on;

	select nombre from TipoMaestro order by nombre

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetListPallet]
@Destino varchar(50)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select p.numero from Pallet p 
	inner join WmsWdGeneral.dbo.estado e on p.destinoId=e.id 
	where e.nombre=@Destino 
	order by p.id

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetListPrealert]
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	select nombre from Prealerta 
	where activo=1

END




go

CREATE PROCEDURE [dbo].[pa_GetListPrealertOms]
AS
BEGIN
  SET NOCOUNT ON;

  SELECT 
    p.Id,
    p.Nombre,
    p.Fecha,
    p.Estado,
    p.UsuarioId,
    p.TipoOrigenId,
    p.OrigenId,
    u.nombres  AS UsuarioNombre
  FROM Prealerta p
  LEFT JOIN UsuarioSys u ON u.Id = p.UsuarioId
  WHERE p.Activo = 1
  ORDER BY p.Id DESC
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetListSapCode]
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetListSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    SELECT codigo FROM CodigoSap 
	ORDER BY codigo

END




go

CREATE PROCEDURE [dbo].[pa_GetListStateInventory]
		AS
		BEGIN

			SET NOCOUNT ON;

			
			SELECT e.padreInventario FROM Estado e
			INNER JOIN Ingreso i ON e.Id = i.estadoId
            WHERE e.PadreInventario IS NOT NULL
            GROUP BY e.PadreInventario 
			ORDER BY e.PadreInventario

		END
go

CREATE PROCEDURE [dbo].[pa_GetMasterDescriptionById]
    @Id INT
    AS
        
    BEGIN
        SELECT Descripcion FROM Maestro
        WHERE id = @Id
    END
go

CREATE PROCEDURE [dbo].[pa_GetMasterDetailById]
    @Id INT
    AS
        
    BEGIN
        SELECT Detalle FROM Maestro
        WHERE id = @Id
    END
go

CREATE PROCEDURE [dbo].[pa_GetMasterNameById]
    @Id INT
    AS
        
    BEGIN
        SELECT Codigo FROM Maestro
        WHERE id = @Id
    END
go


CREATE PROCEDURE [dbo].[pa_GetModelBase]
@Base VARCHAR(50),
@Serial VARCHAR(50)
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Base='BaseDespacho')
	BEGIN
		SELECT serial,codigoSap,estadoSap,estadoRR,Lote FROM dbo.BaseDespacho WITH(NOLOCK)
		WHERE serial=@Serial
	END
	ELSE
	IF(@Base='BaseEmpaque')
	BEGIN
		SELECT serial,codigoSap,estadoSap,estadoRR,Lote FROM dbo.BaseEmpaque WITH(NOLOCK)
		WHERE serial=@Serial
	END
	ELSE
	IF(@Base='BaseInventario')
	BEGIN
		SELECT serial,codigoSap,estadoSap,estadoRR FROM dbo.BaseInventario WITH(NOLOCK)
		WHERE serial=@Serial
	END
	ELSE
	IF(@Base='BaseSeparacion')
	BEGIN
		SELECT serial,codigoSap,estadoSap,estadoRR FROM dbo.BaseSeparacion WITH(NOLOCK)
		WHERE serial=@Serial
	END
	ELSE
	IF(@Base='BaseIngreso')
	BEGIN
		SELECT serial,codigoSap,estadoSap,estadoRR,Lote FROM dbo.BaseIngreso WITH(NOLOCK)
		WHERE serial=@Serial
	END
    IF(@Base='BaseEtiquetado')
	BEGIN
		SELECT serial,codigoSap,estadoSap,estadoRR FROM dbo.BaseEtiquetado WITH(NOLOCK)
		WHERE serial=@Serial
	END

END
go

CREATE   PROCEDURE [dbo].[pa_GetModelBaseWeb]
@Base VARCHAR(50),
@Serial VARCHAR(50)
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Base='BaseDespacho')
	BEGIN
		SELECT serial,codigoSap,estadoSap,estadoRR,Lote FROM dbo.BaseDespacho WITH(NOLOCK)
		WHERE serial=@Serial
	END
	ELSE
	IF(@Base='BaseEmpaque')
	BEGIN
  		SELECT serial,codigoSap,estadoSap,estadoRR,Lote FROM dbo.BaseEmpaque WITH(NOLOCK)
		WHERE serial=@Serial
	END
	ELSE
	IF(@Base='BaseInventario')
	BEGIN
		SELECT serial,codigoSap,estadoSap,estadoRR FROM dbo.BaseInventario WITH(NOLOCK)
		WHERE serial=@Serial
	END
	ELSE
	IF(@Base='BaseSeparacion')
	BEGIN
		SELECT serial,codigoSap,estadoSap,estadoRR FROM dbo.BaseSeparacion WITH(NOLOCK)
		WHERE serial=@Serial
	END
	ELSE
	IF(@Base='BaseIngreso')
	BEGIN
		SELECT serial,codigoSap,estadoSap,estadoRR,Lote FROM dbo.BaseIngreso WITH(NOLOCK)
		WHERE serial=@Serial
	END
    IF(@Base='BaseEtiquetado')
	BEGIN
		SELECT serial,codigoSap,estadoSap,estadoRR FROM dbo.BaseEtiquetado WITH(NOLOCK)
		WHERE serial=@Serial
	END
	end
go

CREATE PROCEDURE [dbo].[pa_GetModelDiagnostic]
			@Serial VARCHAR(50)
			AS
			BEGIN

				SET NOCOUNT ON;

				SELECT TOP 1 serialId, serial, mac, variable1, variable2, variable3, variable4
				FROM Diagnostico
				WHERE serial = @Serial
				ORDER BY serialId DESC;

			END
go


CREATE   PROCEDURE [dbo].[pa_GetModelDispatch]
@Pallet int,
@CajaId int
AS
BEGIN
	SET NOCOUNT ON;

	if(@CajaId=0) BEGIN
	SELECT Id,Serial,Mac,
					 CodigoSapId,
					 PalletId,
					 PalletIdIngreso,
					 CajaDespachoId,
					 TipoOrigenId,
					 OrigenId,
					 TipologiaId,
					 NivelId,
					 Tramite,
					 Documento,
					 Guia,
					 Falla,
					 PrealertaId,
					 Cruce,
					 Novedad,
					 Fecha,
					 SmartCard,
					 smartCardId,
					 loteId,
					 serial3,
					 cajaIngresoId,
					 numeroSmartcard,
					 FallaCosmeticaId,
					 FallaFuncionalId,
					 Causa
	FROM Ingreso WITH(NOLOCK)
	WHERE PalletId=@Pallet END ELSE BEGIN
	SELECT Id,Serial,Mac,
					 CodigoSapId,
					 PalletId,
					 PalletIdIngreso,
					 CajaDespachoId,
					 TipoOrigenId,
					 OrigenId,
					 TipologiaId,
					 NivelId,
					 Tramite,
					 Documento,
					 Guia,
					 Falla,
					 PrealertaId,
					 Cruce,
					 Novedad,
					 Fecha,
					 SmartCard,
					 smartCardId,
					 loteId,
					 serial3,
					 cajaIngresoId,
					 numeroSmartcard,
					 FallaCosmeticaId,
					 FallaFuncionalId,
					 Causa
	FROM Ingreso WITH(NOLOCK)
	WHERE PalletId=@Pallet
	  AND CajaDespachoId=@CajaId END    
END
go


CREATE   PROCEDURE [dbo].[pa_GetModelDispatchAccesory]
    @Pallet INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT Id, CodigoSapid, TipoAccesorio, TipoOrigenId, OrigenId, PalletId, EstadoLimpiezaId, Documento, Observacion, Guia, Fecha, SerialEmpaque, Caja, fechaLimpieza, fechaEmpaque, UsuarioLimpiezaId
    FROM Accesorio WITH(NOLOCK) 
    WHERE PalletId = @Pallet;
END;
go

CREATE PROCEDURE [dbo].[pa_GetModelDispatchBoxAccesory]
        @Pallet int,
        @CajaDespachoId int
        AS
        BEGIN
            SET NOCOUNT ON;
            SELECT a.Id,a.CodigoSapid,a.TipoAccesorio,a.TipoOrigenId,a.OrigenId,a.PalletId,a.EstadoLimpiezaId,a.Documento,a.Observacion,a.Guia,a.Fecha,a.SerialEmpaque,a.Caja,a.fechaLimpieza,a.fechaEmpaque
            FROM Accesorio a WITH(NOLOCK) 
            INNER JOIN  Ingreso i ON a.SerialEmpaque = i.Serial
            WHERE a.PalletId=@Pallet  AND i.CajaDespachoId = @CajaDespachoId
        END
go

CREATE PROCEDURE [dbo].[pa_GetModelEntry]
@Serial VARCHAR(50)
AS
BEGIN

	SET NOCOUNT ON;

    SELECT i.id,i.serial,i.mac,i.Serial3,i.Serial4,i.Serial5,cs.codigo,cs.id AS codigoSapId,cs.descripcion,e.nombre AS estado,e.id AS estadoId,u.nombreusuario,
    i.cajaEmpaqueId,ce.numero AS caja,cd.numero AS cajaDespacho,p.numero AS pallet,p.id AS palletId,po.Numero AS posicion,n.codigo AS nivel,n.id AS nivelId,
    i.tipoOrigenId,i.origenId,i.tipologiaId,t.codigo AS tipologia,i.palletIdIngreso,i.fecha,i.garantiaFabricante,i.falla ,i.loteId, i.PalletIdEmpaque,
    l.nombre as lote,i.smartCardId,i.SmartCard,i.cajaIngresoId,ci.Numero as cajaIngreso,md.Id as modeloId,md.Codigo as modelo
	FROM Ingreso i WITH(NOLOCK) 
	INNER join CodigoSap cs ON i.codigoSapId=cs.id 
	INNER join WmsWdGeneral.dbo.Usuario u ON i.usuarioId=u.id 
	INNER join WmsWdGeneral.dbo.Estado e ON i.estadoId=e.id 
	INNER join Maestro n ON i.nivelId=n.id 
	INNER join Maestro t ON i.tipologiaId=t.id 
	left join Pallet p ON i.PalletId=p.Id 
	left join Posicion po ON p.PosicionId=po.Id 
	left join CajaEmpaque ce ON i.cajaEmpaqueId=ce.id 
	left join CajaDespacho cd ON i.cajaDespachoId=cd.id
    left join cajaIngreso ci ON i.cajaIngresoId = ci.Id 
    left join Lote l ON i.loteId = l.id
    LEFT JOIN maestro md ON i.modeloId = md.Id
	WHERE i.serial=@Serial

END
go

CREATE PROCEDURE [dbo].[pa_GetModelEntryClasificacionEntrega]
	@Serial VARCHAR(50),
    @UsuarioId INT,
    @Perfil VARCHAR(50)

    AS

    BEGIN
        -- SET NOCOUNT ON added to prevent extra result sets from
        -- interfering with SELECT statements.
        SET NOCOUNT ON;

        SELECT i.id,i.serial,i.mac,cs.codigo,cs.id AS codigoSapId,cs.descripcion,e.nombre AS estado,e.id AS estadoId,u.nombreusuario,i.cajaEmpaqueId,ce.numero AS caja,cd.numero AS cajaDespacho,p.numero AS pallet,p.id AS palletId,po.Numero AS posicion,n.codigo AS nivel,n.id AS nivelId,i.tipoOrigenId,i.origenId,i.tipologiaId,t.codigo AS tipologia,i.palletIdIngreso,i.fecha,i.garantiaFabricante,i.falla 
		FROM Ingreso i WITH(NOLOCK) 
		INNER join CodigoSap cs ON i.codigoSapId=cs.id 
		INNER join WmsWdGeneral.dbo.Usuario u ON i.usuarioId=u.id 
		INNER join WmsWdGeneral.dbo.Estado e ON i.estadoId=e.id
		INNER join Maestro n ON i.nivelId=n.id 
		INNER join Maestro t ON i.tipologiaId=t.id 
		left join Pallet p ON i.PalletId=p.Id 
		left join Posicion po ON p.PosicionId=po.Id 
		left join CajaEmpaque ce ON i.cajaEmpaqueId=ce.id 
		left join CajaDespacho cd ON i.cajaDespachoId=cd.id 
		WHERE i.serial= @Serial		
        AND (@Perfil = 'ADMINISTRADOR' OR (i.UsuarioIdMovimiento = @UsuarioId AND @Perfil <> 'ADMINISTRADOR'));
    END
    
go

CREATE PROCEDURE [dbo].[pa_GetModelFinishInventory]
    AS

    BEGIN
        SET NOCOUNT ON;

        SELECT id,serial,codigoSap,codigoSapReal,pallet,palletReal,estadoId,estadoSap,estadoRR,ajuste,fecha,usuarioId,serialId,mac,sobrante
        FROM Inventario WITH(NOLOCK) 
    END
go


CREATE PROCEDURE [dbo].[pa_GetModelLabel]
@nombre VARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
 
	SET NOCOUNT ON;
 
	SELECT e.id,e.nombre,e.tipo,e.impresion,e.codigoSapId,cs.codigo,cs.descripcion, e.activo
    FROM Etiqueta e
    LEFT JOIN CodigoSap cs ON e.codigoSapId = cs.Id
    WHERE e.nombre=@nombre	
 
END	;
go


CREATE PROCEDURE [dbo].[pa_GetModelMaster]
    @CodigoSap varchar(50)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT m.codigo
    FROM Maestro m
    INNER JOIN CodigoSap cs
        ON m.id = cs.modeloId
    WHERE cs.codigo = @CodigoSap
      AND m.activo = 1;
END;
go

CREATE PROCEDURE [dbo].[pa_GetModelPallet]
@Id int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    select p.id,p.numero,po.id posicionId,po.numero as posicion,t.id as tipologiaId,t.codigo as tipologia,cs.id as codigoSapId,cs.codigo as codigoSap,cs.descripcion,o.id as origenId,o.nombre as origen,d.id as destinoId,p.activo,u.nombreUsuario,p.loteId,CONVERT(varchar, p.Fecha, 23) as fecha,cs.multimodelo,cs.cantidadCaja,cs.smartcard from Pallet p 
	left join Posicion po on p.posicionId=po.id 
	inner join Maestro t on p.tipologiaId=t.id 
	left join CodigoSap cs on p.codigoSapId=cs.id 
	left join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
	left join WmsWdGeneral.dbo.estado d on p.destinoId=d.id 
	inner join WmsWdGeneral.dbo.usuario u on p.usuarioId=u.id 
	where p.id=@Id

END
go

CREATE PROCEDURE [dbo].[pa_GetModelPalletBoxDispatch]
        @Pallet int,
        @CajaId int
        AS
        BEGIN
            SELECT i.Id,i.Serial,i.Mac,i.CodigoSapId,i.PalletId,i.PalletIdEmpaque,i.CajaDespachoId,i.TipoOrigenId,i.OrigenId,i.TipologiaId,i.NivelId,i.Tramite,i.Documento,i.Guia,i.Falla,i.PrealertaId,i.Cruce,i.Novedad,i.Fecha
            FROM Ingreso  i WITH(NOLOCK) 
            INNER JOIN Pallet p ON i.palletId = p.Id
            INNER JOIN estado d ON p.destinoId=d.id 
            WHERE i.PalletId=@Pallet and i.CajaDespachoId=@CajaId and d.Nombre = 'PREDESPACHO'
        END
go

CREATE PROCEDURE [dbo].[pa_GetModelPalletBoxPacking]
        @Pallet int,
        @CajaId int
        AS
        BEGIN
            SELECT Id,Serial,Mac,CodigoSapId,PalletId,PalletIdEmpaque,CajaEmpaqueId,TipoOrigenId,OrigenId,TipologiaId,NivelId,Tramite,Documento,Guia,Falla,PrealertaId,Cruce,Novedad,Fecha
            FROM Ingreso WITH(NOLOCK) 
            WHERE PalletIdEmpaque=@Pallet and CajaEmpaqueId=@CajaId
        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetModelPalletByNumber]
@Number varchar(50)
AS
/*
DECLARE @Number varchar(50)
SET @Number = '190902184742'
EXEC pa_GetModelPalletByNumber @Number
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    SELECT DISTINCT p.id,p.numero,po.id posicionId,po.numero as posicion,t.id as tipologiaId,t.codigo as tipologia,cs.id as codigoSapId,cs.codigo as codigoSap,cs.descripcion,o.id as origenId,o.nombre as origen,d.id as destinoId,p.activo,u.nombreUsuario 
	FROM dbo.Pallet p WITH(NOLOCK)
	left join dbo.Posicion po WITH(NOLOCK) on p.posicionId=po.id 
	inner join dbo.Maestro t WITH(NOLOCK) on p.tipologiaId=t.id 
	left join dbo.CodigoSap cs WITH(NOLOCK) on p.codigoSapId=cs.id 
	left join dbo.Estado o on p.origenId=o.id 
	left join dbo.Estado d on p.destinoId=d.id 
	inner join dbo.Usuario u on p.usuarioId=u.id 
	WHERE p.Numero=@Number

END
go

CREATE PROCEDURE [dbo].[pa_GetModelPqrs]
        @Serial VARCHAR(50),
        @Stage INT
        AS
        BEGIN

            SET NOCOUNT ON;

            SELECT 
                [id],
                [id_company],
                [company_name],
                [id_team],
                [team_name],
                [id_user],
                [user_name],
                [create_date],
                [x_studio_area_responsable_id],
                [x_studio_area_responsable_name],
                [tipo_de_cliente_pqr_id],
                [tipo_de_cliente_pqr_name],
                [x_studio_placa],
                [refurbish_pqr_id],
                [refurbish_pqr_name],
                [tecnologia_pqr_id],
                [tecnologia_pqr_name],
                [ticket_ref],
                [description],
                [name],
                [x_studio_cantidad_de_equipos],
                [clasificacion_refurbish_id],
                [clasificacion_refurbish_name],
                [x_studio_metodo_de_contacto],
                [x_studio_fecha_llegada_unidades],
                [x_studio_fecha_reporte],
                [x_studio_metodo_de_contacto_final],
                [x_studio_tramite],
                [x_studio_nivel_pqr],
                [partner_id],
                [partner_name],
                [partner_email],
                [attachment_url],
                [id_stage],
                [stage_name],
                [tag_ids],
                [x_studio_pais],
                [x_studio_observaciones],
                [x_studio_diagnstico_tcnico_woden],
                [Id_diagnostico],
                [SerialId],
                [CodigosapId],
                [FalladiagnosticoId],
                [EstadoDiagnosticoId],
                [FechaDiagnostico]
            FROM 
                [dbo].[PQRS]
            WHERE 
                [name] = @Serial
            AND
                [id_stage] = @Stage;

        END
go

CREATE PROCEDURE [dbo].[pa_GetModelPrealert]
@Nombre varchar(50)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	select p.id,p.nombre,m.codigo as tipoOrigen,ma.codigo as origen,p.guia,p.tipologiaId from Prealerta p 
	inner join Maestro m on p.tipoOrigenId=m.id inner join Maestro ma on p.origenId=ma.id 
	where p.nombre=@Nombre

END
go


CREATE PROCEDURE [dbo].[pa_GetModelPrealertSerial]
@PrealertaId int,
@Serial varchar(50)
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	SET NOCOUNT ON;

	select id,serial,codigosap,tramite,pedido,caja,falla,tecnicoCliente,novedad,garantia,isnull(recogida,0) recogida,mac,loteId from PrealertaSerial 
	where prealertaId=@PrealertaId and serial=@Serial

END

go



CREATE PROCEDURE [dbo].[pa_GetModelPrealertSerialByCodSap]
@PrealertaId int,
@CodigoSap varchar(50)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	SET NOCOUNT ON;

	select id,serial,codigosap,tramite,pedido,caja,falla,tecnicoCliente,novedad,garantia,isnull(recogida,0) recogida,mac from PrealertaSerial 
	where prealertaId=@PrealertaId and codigosap=@CodigoSap

END

go


CREATE PROCEDURE [dbo].[pa_GetModelSapCode]
@Codigo VARCHAR(20)
AS

BEGIN
	SET NOCOUNT ON;

    select cs.id,cs.codigo,cs.descripcion,f.id as familiaId,f.codigo as familia,t.id as tipoId,t.codigo as tipo,cs.validacion,cs.direccion,cs.largos,cs.recorte,cs.reingreso,cs.numSerial,cs.AsignacionAcc,cs.LargosMac,cs.LargosSerial3,cs.LargosSerial4,cs.LargosSerial5,c.Codigo as clasificacion,cs.validacionMac,cs.direccionMac,cs.recorteMac,cs.AsignacionFalla,cs.multimodelo,cs.cantidadCaja from CodigoSap cs 
	inner join Maestro f on cs.familiaId=f.id 
	inner join Maestro t on cs.tipoId=t.id 
    inner join Maestro c on cs.ClasificacionId = c.id
	where cs.codigo=@Codigo

END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetNameWorflowDestiny]
@Opcion varchar(20),
@Origen varchar(50),
@Descripcion varchar(50),
@TipologiaId int
AS
/*
DECLARE @Opcion varchar(20)
DECLARE @Origen varchar(50)
DECLARE @Descripcion varchar(50)
DECLARE @TipologiaId int
SET @Opcion ='Opc_Recibo'
SET @Origen='P-SEPARACION'
SET @Descripcion ='Separacion'
SET @TipologiaId =269
EXEC pa_GetIdWorflowDestiny @Opcion,@Origen,@Descripcion,@TipologiaId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	set nocount on;

	IF(@Opcion='NINGUNA')
	BEGIN
		select d.Nombre from WorkFlowDestino wd 
		inner join WorkFlow w on wd.workFlowId=w.id 
		inner join WmsWdGeneral.dbo.estado d on wd.destinoId=d.id 
		inner join WmsWdGeneral.dbo.estado o on w.origenId=o.id 
		where o.nombre=@Origen and w.OpcionId=0 and w.tipologiaId=@TipologiaId
	END
	ELSE
	BEGIN
		select d.Nombre from WorkFlowDestino wd 
		inner join WorkFlow w on wd.workFlowId=w.id 
		inner join WmsWdGeneral.dbo.estado d on wd.destinoId=d.id 
		inner join WmsWdGeneral.dbo.estado o on w.origenId=o.id 
		left join WmsWdGeneral.dbo.permiso op on w.opcionId=op.id 
		where o.nombre=@Origen and op.nombre=@Opcion and op.descripcion=@Descripcion and w.tipologiaId=@TipologiaId
	END

END




go


CREATE PROCEDURE [dbo].[pa_GetOriginsMaster]
    @Tipo varchar(50)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT m.codigo
    FROM Maestro m
    INNER JOIN TipoMaestro tm
        ON m.tipoMaestroId = tm.id
    WHERE tm.nombre = 'Origenes'
      AND m.descripcion = @Tipo
      AND m.activo = 1
    ORDER BY m.codigo;
END;
go

CREATE PROCEDURE [dbo].[pa_GetPackedAccesoriesSerials]
    @CodigoSap VARCHAR(20),
    @TipoAccesorio VARCHAR(20),
    @PalletId INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT SerialEmpaque
    FROM Accesorio
    WHERE CodigoSapId = (SELECT id FROM CodigoSap where Codigo = @CodigoSap)
      AND TipoAccesorio = @TipoAccesorio
      AND PalletId = @PalletId
	  AND SerialEmpaque <> '0'
	  AND EstadoId = (SELECT id FROM Estado where Nombre = 'EMPACADO');
END
go

CREATE PROCEDURE [dbo].[pa_GetPalletByCustomer]
	@ClienteId int,
	@PalletId int
AS
/*
DECLARE @ClienteId int
SET @ClienteId = 1
DECLARE @PalletId int
SET @PalletId = 12
EXEC pa_GetPalletByCustomer @ClienteId, @PalletId
*/
BEGIN
DECLARE @Cliente nvarchar(20)
DECLARE @Quantity int
DECLARE @Origen varchar(30)
DECLARE @Destino varchar(30)
DECLARE @DestinoId int

CREATE TABLE #tempEnds (Id int IDENTITY (1,1) NOT NULL, CustomerId int, Customer nvarchar(30), Position nvarchar(30), Date datetime, Number varchar(20),
Source nchar(30), Destiny nchar(30), Typology nchar(30), CodeSap varchar(20), Model nvarchar(40), Card nvarchar(20), UserName nvarchar(20), Quantity int)

SELECT TOP 1 p.id,p.numero,cs.codigo,cs.descripcion,t.codigo as tipologia,o.Nombre as origen,d.nombre as destino,u.nombreusuario,p.fecha,po.numero as posicion, Tarjeta=''
INTO #temp
FROM dbo.Pallet p 
FULL OUTER JOIN dbo.Posicion po on p.posicionId=po.id 
INNER JOIN dbo.CodigoSap cs on p.codigoSapId=cs.id 
INNER JOIN dbo.Maestro t on p.TipologiaId=t.Id 
INNER JOIN dbo.Estado o on p.origenId=o.id 
INNER JOIN dbo.Estado d on p.destinoId=d.id 
INNER JOIN dbo.Usuario u on p.usuarioId=u.id 
WHERE p.id = @PalletId

SELECT @Cliente = Nombre FROM dbo.Cliente WHERE Id = @ClienteId
SELECT @Origen = origen FROM #temp

IF(@Origen = 'SERIALIZADO')
BEGIN
	SELECT @Quantity = ISNULL(COUNT(*),0) FROM Serializado WHERE PalletId = @PalletId
END

IF(@Origen = 'INGRESO')
BEGIN
	SELECT @Quantity = ISNULL(COUNT(*),0) FROM Ingreso WHERE PalletId = @PalletId
END

INSERT INTO #tempEnds (CustomerId, Customer, Position, Date, Number, Source, Destiny, Typology, CodeSap, Model, Card, UserName, Quantity)
SELECT @ClienteId, @Cliente, Posicion, Fecha, Numero, Origen, Destino, Tipologia, Codigo, Descripcion, Tarjeta, NombreUsuario, @Quantity FROM #temp

SELECT * FROM #tempEnds

DROP TABLE #temp
DROP TABLE #tempEnds
END
go


CREATE PROCEDURE pa_GetPalletNumero
    @NumeroPallet VARCHAR(255) -- Parámetro de entrada para el número de pallet
AS
BEGIN
    SET NOCOUNT ON;

    -- Verificar si el número de pallet existe
    IF EXISTS (
        SELECT 1
        FROM Pallet
        WHERE Numero = @NumeroPallet
    )
    BEGIN
        -- Retornar TRUE si el número de pallet existe
        SELECT CAST(1 AS BIT) AS Existe;
    END
    ELSE
    BEGIN
        -- Retornar FALSE si el número de pallet no existe
        SELECT CAST(0 AS BIT) AS Existe;
    END
END;

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID('Pallet') AND name = 'IX_Pallet_Numero'
)
BEGIN
    -- Crear un índice en la columna 'Numero' para mejorar el rendimiento
    CREATE NONCLUSTERED INDEX IX_Pallet_Numero ON Pallet (Numero);
END;
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetPalletsByPosicion]
@posicionId int
AS
BEGIN
	SET NOCOUNT ON;

	SELECT p.Id,p.Numero,count(i.PalletId)as cantidad FROM Pallet p LEFT JOIN Ingreso i ON p.Id=i.PalletId WHERE PosicionId=@posicionId GROUP BY p.Id,p.Numero
END

go

-- =============================================
-- Author:		Julián Gómez Ramírez
-- Create date: 29/07/2019
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetPeportsQuery]
	@ClienteId int,
	@Id int,
	@Where varchar(100) = ''
AS
/*
DECLARE @ClienteId int
SET @ClienteId = 12
DECLARE @Id int
SET @Id = 170
DECLARE @Where varchar(100)
SET @Where = '@FechaInicial:20190601,@FechaFinal:20191005'
SET @Where = '@FechaInicial:20191118'
SET @Where = ''
EXEC pa_GetPeportsQuery @ClienteId,@Id,@Where
*/
BEGIN
SET NOCOUNT ON;
	DECLARE @Count INT = 1
	DECLARE @Counter INT = 1
	DECLARE @CounterF INT = 1
	DECLARE @Item VARCHAR(200)
	DECLARE @CQuery VARCHAR(MAX)
	DECLARE @Query VARCHAR(MAX)
	DECLARE @FechaIni VARCHAR(10)
	DECLARE @FechaFin VARCHAR(10)
	DECLARE @Variable VARCHAR(20)
	DECLARE @Valor VARCHAR(20)
	DECLARE @VariableF VARCHAR(20)
	DECLARE @ValorF VARCHAR(20)
	DECLARE @Fecha VARCHAR(10)
	DECLARE @CCampo VARCHAR(100) = ''
	DECLARE @Campo VARCHAR(100) = ''
	DECLARE @IdCampo INT = 0
	DECLARE @TFiltro INT = 0

	SELECT InformeId,Campo INTO #TempCampo FROM dbo.InformeCampo WITH(NOLOCK) WHERE InformeId = @Id
	ALTER TABLE #TempCampo ADD Orden INT IDENTITY(1,1) NOT NULL
	CREATE TABLE #TempReporte(Id INT NOT NULL IDENTITY(1,1))	
	WHILE @Count <= (SELECT COUNT(*) FROM #TempCampo)
	BEGIN
		DECLARE @EXIS INT = 0
		SELECT @Campo = Campo FROM #TempCampo WHERE Orden = @Count
		SELECT @EXIS = PATINDEX('fech%',LOWER(LTRIM(RTRIM(@Campo))))
		IF(@EXIS > 0)
		BEGIN
			SET @CQuery = 'ALTER TABLE #TempReporte ADD ' + LOWER(@Campo) + ' DATE NULL'
			EXEC(@CQuery)
			SET @Campo = LOWER(@Campo)
		END
		ELSE
		BEGIN
			SET @CQuery =  'ALTER TABLE #TempReporte ADD ' + LOWER(@Campo) + ' VARCHAR(100) NULL'
			EXEC(@CQuery)
			SET @Campo = LOWER(@Campo)
		END
		IF(@CCampo='')
		SET @CCampo = LOWER(@Campo)
		ELSE
			SET @CCampo = @CCampo + ','+ LOWER(@Campo)

		SET @Count = @Count + 1
	END
	
	SELECT @TFiltro = COUNT(*) FROM dbo.Filtro WITH(NOLOCK) WHERE Id IN (SELECT FiltroId FROM dbo.InformeFiltro WITH(NOLOCK) WHERE InformeId = @Id)

	IF(@Where <> '' AND @TFiltro > 0)
	BEGIN
		CREATE TABLE #TempTFiltro(Id INT IDENTITY(1,1) NOT NULL,Variable NVARCHAR(20),Valor NVARCHAR(20))
		SELECT * INTO #TempFiltro FROM dbo.Filtro WITH(NOLOCK) WHERE Id IN (SELECT FiltroId FROM dbo.InformeFiltro WITH(NOLOCK) WHERE InformeId = @Id)
		SELECT * INTO #TempWhere FROM dbo.fnSplitString(RTRIM(LTRIM(@Where)), ',')
		ALTER TABLE #TempWhere ADD Orden INT IDENTITY(1,1) NOT NULL

		WHILE @Counter <= (SELECT COUNT(*) FROM #TempWhere)
		BEGIN
			SELECT @Item = RTRIM(LTRIM(Item)) FROM #TempWhere WHERE Orden = @Counter
			SELECT @Variable = SUBSTRING(@Item, 1, CHARINDEX(':', @Item)-1) 

			IF EXISTS(SELECT * FROM #TempFiltro WHERE RTRIM(LTRIM(Nombre)) = RTRIM(LTRIM(@Variable)))
			BEGIN
				SET @Valor = SUBSTRING(@Item, CHARINDEX(':', @Item)+1, (LEN(@Item) - CHARINDEX(':', @Item)+1)) 
				INSERT INTO #TempTFiltro(Variable, Valor) VALUES(@Variable,@Valor)
			END
			SET @Counter = @Counter + 1
		END

		SELECT @Query = Consulta FROM dbo.Informe WITH(NOLOCK) WHERE Id = @Id AND ClienteId = @ClienteId
		
		WHILE @CounterF <= (SELECT COUNT(*) FROM #TempTFiltro)
		BEGIN
			SELECT @VariableF = RTRIM(LTRIM(Variable)), @ValorF = Valor FROM #TempTFiltro WHERE Id = @CounterF
			IF EXISTS(SELECT * FROM #TempTFiltro WHERE RTRIM(LTRIM(Variable)) = RTRIM(LTRIM(@VariableF)))
			BEGIN
				SET @Fecha = CHAR(39) + @ValorF + CHAR(39)
				SET @Query = REPLACE(@Query,@VariableF,@Fecha)
			END
			SET @CounterF = @CounterF + 1
		END
		PRINT @Query

		INSERT INTO #TempReporte
		EXEC(@Query)
		
		DROP TABLE #TempTFiltro
		DROP TABLE #TempFiltro
		DROP TABLE #TempWhere
	END
	ELSE
	BEGIN
		IF(@TFiltro = 0)
		BEGIN
			SELECT @Query = Consulta FROM dbo.Informe WITH(NOLOCK) WHERE Id = @Id AND ClienteId = @ClienteId
			INSERT INTO #TempReporte
			EXEC(@Query)
		END
	END

	SELECT * --INTO TempReporte
	FROM #TempReporte

	DROP TABLE #TempCampo
	DROP TABLE #TempReporte
END

go

CREATE   PROCEDURE [dbo].[pa_GetPercentDiff]
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
 
    SET NOCOUNT ON;
 
    SELECT
        CAST((SELECT COUNT(id) FROM Inventario where sobrante = 0) AS FLOAT) /
        CAST(COUNT(I.ID) AS FLOAT) AS Resultado
    FROM
        Ingreso I 
END
go



CREATE   PROCEDURE [dbo].[pa_GetPercentDiffSap]
    @CodigoSapId INT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @IngresoTotal FLOAT = (
        SELECT COUNT(Id)
        FROM Ingreso
        WHERE CodigoSapId = @CodigoSapId
    );

    DECLARE @InventarioTotal FLOAT = (
        SELECT COUNT(inv.Id)
        FROM Inventario inv
        INNER JOIN CodigoSap cs ON cs.Codigo = inv.CodigoSap
        WHERE inv.Sobrante = 1 AND cs.Id = @CodigoSapId
    );

    SELECT CASE WHEN @IngresoTotal = 0 THEN 0 ELSE (@InventarioTotal / @IngresoTotal) END AS Resultado;
END
go


    CREATE PROCEDURE [dbo].[pa_GetProactiveRepair]
		@Serial varchar(50),
        @Filas INT OUT
		AS
		BEGIN
			-- SET NOCOUNT ON added to prevent extra result sets from
			-- interfering with SELECT statements.
			
			set nocount on;
			SELECT  d.serial FROM Despacho d
            INNER JOIN Diagnostico dg ON d.SerialId=dg.SerialId
            INNER JOIN maestro ef ON dg.EstadoFinalId = ef.Id
            WHERE ef.Codigo ='REPARACION PROACTIVA'
            AND d.Serial = @Serial
            AND d.SerialId = (SELECT MAX(SerialId) FROM Diagnostico WHERE Serial = @Serial)
            AND DATEDIFF(DAY, (d.fecha), GETDATE()) < 180
            SELECT @Filas = @@ROWCOUNT
		END
go

CREATE PROCEDURE [dbo].[pa_GetProactiveRepairAll]

		AS

		BEGIN

			-- SET NOCOUNT ON added to prevent extra result sets from

			-- interfering with SELECT statements.

			

			set nocount on;

			WITH UltimoDiagnostico AS (
    SELECT dg.SerialId, dg.EstadoFinalId,
           ROW_NUMBER() OVER (PARTITION BY dg.SerialId ORDER BY dg.SerialId DESC) AS rn
    FROM Diagnostico dg
)
SELECT d.Serial
FROM Despacho d
INNER JOIN UltimoDiagnostico ud
    ON d.SerialId = ud.SerialId AND ud.rn = 1
INNER JOIN Maestro ef
    ON ud.EstadoFinalId = ef.Id
WHERE ef.Codigo = 'REPARACION PROACTIVA'
  AND d.Fecha >= DATEADD(DAY, -180, GETDATE())

		END
go


       CREATE PROCEDURE [dbo].[pa_GetProviderDescriptionMaster]
		@CodigoSap varchar(50)
		AS
		BEGIN
			-- SET NOCOUNT ON added to prevent extra result sets from
			-- interfering with SELECT statements.

			SET NOCOUNT ON;

			SELECT m.descripcion FROM Maestro m 
			INNER JOIN CodigoSap cs ON m.id=cs.proveedorId 
			WHERE cs.codigo=@CodigoSap
		END
go


       CREATE PROCEDURE [dbo].[pa_GetProviderMaster]
		@CodigoSap varchar(50)
		AS
		BEGIN
			-- SET NOCOUNT ON added to prevent extra result sets from
			-- interfering with SELECT statements.

			SET NOCOUNT ON;

			SELECT m.codigo FROM Maestro m 
			INNER JOIN CodigoSap cs ON m.id=cs.proveedorId 
			WHERE cs.codigo=@CodigoSap
		END
go


CREATE   PROCEDURE [dbo].[pa_GetRepairUser]
@Perfil VARCHAR(20),
@UsuarioId INT
AS
BEGIN
	set nocount on;
	WITH UltimaReparacion AS (
		SELECT
			r.Id, r.SerialId, r.CodigoSapId, r.TecnicoAsignacionId, r.UsuarioId, r.FechaAsignacion,
			ROW_NUMBER() OVER (PARTITION BY r.SerialId ORDER BY r.FechaAsignacion DESC) AS rn
		FROM Reparacion r
		WHERE @Perfil = 'ADMINISTRADOR' OR r.UsuarioId = @UsuarioId
	)
	SELECT
		i.serial, i.mac, cs.Codigo, cs.descripcion, u.nombre_usuario
	FROM UltimaReparacion ur
	INNER JOIN Ingreso i ON ur.SerialId = i.Id
	INNER JOIN WmsWdGeneral.dbo.Estado e ON i.estadoId = e.Id
	INNER JOIN CodigoSap cs ON ur.CodigoSapId = cs.Id
	INNER JOIN UsuarioSys u ON ur.TecnicoAsignacionId = u.id
	WHERE ur.rn = 1
	  AND e.Nombre = 'REPARACION-PREASIGNADO'
	ORDER BY ur.FechaAsignacion DESC
END
go

 
CREATE   PROCEDURE [dbo].[pa_GetSapCodeFailureAsig]
    @CodigoSap NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
 
    SELECT Asignacionfalla
    FROM codigosap
    WHERE Codigo = @CodigoSap;
END
go


CREATE   PROCEDURE [dbo].[pa_GetScrapUser]
    @Perfil VARCHAR(20),
    @UsuarioId INT
AS
BEGIN
    SET NOCOUNT ON;
   WITH Ultimos AS (
        SELECT i.Id,i.Serial,i.Mac,i.Serial3,i.Serial4,i.Serial5,cs.Codigo AS CodigoSap,cs.Descripcion,i.NivelId,
        i.LoteId,le.Nombre as Lote,ml.codigo as Modelo,i.Fecha,ROW_NUMBER() OVER (PARTITION BY i.Id ORDER BY i.Id DESC) AS rn
        FROM Ingreso i
        INNER JOIN Estado es ON i.EstadoId = es.Id
        INNER JOIN CodigoSap cs ON i.CodigoSapId = cs.Id
        INNER JOIN UsuarioSys u ON i.UsuarioId = u.Id
		LEFT JOIN Maestro ml ON ml.Id =i.modeloid
		LEFT JOIN lote le ON i.loteid = le.Id
        WHERE es.Nombre = 'SCRAP-PREASIGNADO'
          AND (@Perfil = 'ADMINISTRADOR' OR i.UsuarioIdMovimiento = @UsuarioId))
    SELECT * FROM Ultimos
    WHERE rn = 1
    ORDER BY Id DESC;
END
go

CREATE PROCEDURE [dbo].[pa_GetSerialByMac]
        @Mac VARCHAR(50)
        AS
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.
            SET NOCOUNT ON;

            SELECT i.serial
            FROM Ingreso i WITH(NOLOCK) 
            WHERE i.mac=@Mac
        END
go


CREATE PROCEDURE [dbo].[pa_GetSourceIncome]
	@ntype nvarchar(20),
	@nstate nvarchar(100)
AS
/*
DECLARE @type nvarchar(20)
DECLARE @nstatus nvarchar(100)
SET @type = 'ESTADO' --'CODIGOSAP'
SET @nstatus = 'INGRESO'
EXEC pa_GetSourceIncome @type, @nstatus
*/
BEGIN
SET NOCOUNT ON;
	
	DECLARE @Status nvarchar(100)
	SET @Status = '%' + @nstate + '%'
	
	IF(@ntype = 'ESTADO')
	BEGIN
		SELECT Id = IDENTITY(int,1,1), E.Nombre AS Origen, COUNT(*) AS Cantidad
		INTO #tempTableE
		FROM Ingreso I
		INNER JOIN Pallet P ON I.PalletId = P.Id
		INNER JOIN [WmsWdGeneral].[dbo].[Estado] E ON P.OrigenId = E.Id
		WHERE E.Nombre LIKE @Status
		GROUP BY E.Nombre

		SELECT Id, Origen, Cantidad FROM #tempTableE
		DROP TABLE #tempTableE
	END
	ELSE
		IF(@ntype = 'CODIGOSAP')
		BEGIN
			SELECT Id = IDENTITY(int,1,1), C.Codigo AS Origen, COUNT(*) AS Cantidad
			INTO #tempTableC
			FROM Ingreso I
			INNER JOIN Pallet P ON I.PalletId = P.Id
			INNER JOIN CodigoSap C ON I.CodigoSapId = C.Id 
			INNER JOIN [WmsWdGeneral].[dbo].[Estado] E ON P.OrigenId = E.Id
			WHERE E.Nombre LIKE @Status
			GROUP BY C.Codigo

			SELECT Id, Origen, Cantidad FROM #tempTableC
			DROP TABLE #tempTableC
		END
END




go


CREATE PROCEDURE [dbo].[pa_GetStateIncome]
	@ntype nvarchar(20),
	@nstate nvarchar(100)
AS
/*
DECLARE @type nvarchar(20)
DECLARE @nstatus nvarchar(100)
SET @type = 'FAMILIA' -- 'CODIGOSAP' --'ESTADO' 
SET @nstatus = 'ALMA'
EXEC pa_GetStateIncome @type, @nstatus
*/
BEGIN
SET NOCOUNT ON;
	
	DECLARE @Status nvarchar(100)
	SET @Status = '%' + @nstate + '%'
	
	IF(@ntype = 'ESTADO')
	BEGIN
		SELECT Id = IDENTITY(int,1,1), E.Nombre AS Origen, COUNT(*) AS Cantidad
		INTO #tempTableE
		FROM Ingreso I
		INNER JOIN [WmsWdGeneral].[dbo].[Estado] E ON I.EstadoId = E.Id
		WHERE E.Nombre LIKE @Status
		GROUP BY E.Nombre

		IF EXISTS (SELECT * from #tempTableE)
		BEGIN
			SELECT Id, Origen, Cantidad FROM #tempTableE
		END
		ELSE
		BEGIN
			INSERT INTO #tempTableE (Origen, Cantidad) VALUES ('ESTADO', 0);
			SELECT Id, Origen, Cantidad FROM #tempTableE
		END

		DROP TABLE #tempTableE
	END
	ELSE
		IF(@ntype = 'CODIGOSAP')
		BEGIN
			SELECT Id = IDENTITY(int,1,1), C.Codigo AS Origen, COUNT(*) AS Cantidad
			INTO #tempTableC
			FROM Ingreso I
			INNER JOIN CodigoSap C ON I.CodigoSapId = C.Id 
			INNER JOIN [WmsWdGeneral].[dbo].[Estado] E ON I.EstadoId = E.Id
			WHERE E.Nombre LIKE @Status
			GROUP BY C.Codigo

			IF EXISTS (SELECT * from #tempTableC)
			BEGIN
				SELECT Id, Origen, Cantidad FROM #tempTableC
			END
			ELSE
			BEGIN
				INSERT INTO #tempTableC (Origen, Cantidad) VALUES ('CODIGOSAP', 0);
				SELECT Id, Origen, Cantidad FROM #tempTableC
			END
			DROP TABLE #tempTableC
		END
		ELSE
			IF(@ntype = 'FAMILIA')
			BEGIN
				SELECT Id = IDENTITY(int,1,1), M.Codigo AS Origen, COUNT(*) AS Cantidad
				INTO #tempTableF
				FROM Ingreso I
				INNER JOIN CodigoSap C ON I.CodigoSapId = C.Id
				INNER JOIN Maestro M ON C.FamiliaId = M.Id
				INNER JOIN [WmsWdGeneral].[dbo].[Estado] E ON I.EstadoId = E.Id
				WHERE E.Nombre LIKE @Status
				GROUP BY M.Codigo

				IF EXISTS (SELECT * from #tempTableF)
				BEGIN
					SELECT Id, Origen, Cantidad FROM #tempTableF
				END
				ELSE
				BEGIN
					INSERT INTO #tempTableF (Origen, Cantidad) VALUES ('FAMILIA', 0);
					SELECT Id, Origen, Cantidad FROM #tempTableF
				END
				DROP TABLE #tempTableF
			END
END




go


CREATE PROCEDURE [dbo].[pa_GetTipologyMaster]
    @Descripcion1 varchar(50),
    @Descripcion2 varchar(50),
    @Descripcion3 varchar(50),
    @Descripcion4 varchar(50)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT codigo
    FROM Maestro
    WHERE activo = 1
      AND (
            descripcion = @Descripcion1
         OR descripcion = @Descripcion2
         OR descripcion = @Descripcion3
         OR descripcion = @Descripcion4
      )
    ORDER BY codigo;
END;
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetTotalPrealert]
@Id int
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	select count(serial)total from PrealertaSerial where prealertaId=@Id

END




go


CREATE PROCEDURE [dbo].[pa_GetTrackingSerie]
	@Serial varchar(50)
AS
/*
EXEC pa_GetTrackingSerie '001'
*/
BEGIN
DECLARE @SerialId int
DECLARE @Ciclo int

SELECT DISTINCT SerialId 
INTO #TempCiclo
FROM Movimiento 
WHERE Serial=@Serial 
GROUP BY SerialId

SELECT DISTINCT @Ciclo = COUNT(SerialId) FROM #TempCiclo

CREATE TABLE #TempMovs (Id int, Ciclo int, SerialId int, Serial varchar(50),
Fecha date, FechaMovimiento datetime, Estado varchar(50), Origen varchar(50),
Destino varchar(50), NombreUsuario nvarchar(20), Descripcion varchar(100), Icono varchar(50))

--Consulta detalle ciclo:
IF(@Ciclo = 1)
BEGIN
	SELECT DISTINCT @SerialId = SerialId FROM Movimiento WHERE Serial=@Serial GROUP BY SerialId

	SELECT DISTINCT Id = IDENTITY(int,1,1), @Ciclo AS Ciclo, m.SerialId,m.Serial,Fecha=CONVERT(date,m.Fecha,101),m.Fecha as FechaMovimiento,UPPER(md.Nombre) as Estado,o.Nombre as Origen,d.Nombre as Destino,u.NombreUsuario,m.Descripcion, md.Icono 
	INTO #tempTableM
	FROM Movimiento m 
	INNER JOIN WmsWdGeneral.dbo.Estado o on m.OrigenId=o.id
	INNER JOIN WmsWdGeneral.dbo.Estado d on m.DestinoId=d.Id
	INNER JOIN WmsWdGeneral.dbo.Usuario u on m.UsuarioId=u.Id
	INNER JOIN WmsWdGeneral.dbo.Modulo md on d.ModuloId = md.Id
	WHERE m.SerialId=@SerialId 
	GROUP BY m.fecha, m.Id, m.SerialId, m.Serial, md.Nombre, o.Nombre, d.Nombre, u.NombreUsuario, m.Descripcion, md.Icono

	SELECT * FROM #tempTableM

	DROP TABLE #tempTableM
END
ELSE
BEGIN
	DECLARE @Id int = 1
	DECLARE @Cur int = 1
	
	DECLARE InsIncome CURSOR
		FOR SELECT SerialId FROM #TempCiclo ORDER BY SerialId
		OPEN InsIncome
		FETCH NEXT FROM InsIncome
		INTO @SerialId
		WHILE(@@FETCH_STATUS = 0)
		BEGIN			
			INSERT INTO #TempMovs
			SELECT DISTINCT @Id as Id, @Cur AS Ciclo, m.SerialId,m.Serial,Fecha=CONVERT(date,m.Fecha,101),m.Fecha as FechaMovimiento,
			UPPER(md.Nombre) as Estado,o.Nombre as Origen,d.Nombre as Destino,u.NombreUsuario,m.Descripcion, md.Icono 
			FROM Movimiento m 
			INNER JOIN WmsWdGeneral.dbo.Estado o on m.OrigenId=o.id
			INNER JOIN WmsWdGeneral.dbo.Estado d on m.DestinoId=d.Id
			INNER JOIN WmsWdGeneral.dbo.Usuario u on m.UsuarioId=u.Id
			INNER JOIN WmsWdGeneral.dbo.Modulo md on d.ModuloId = md.Id
			WHERE m.SerialId=@SerialId 
			GROUP BY m.fecha, m.Id, m.SerialId, m.Serial, md.Nombre, o.Nombre, d.Nombre, u.NombreUsuario, m.Descripcion, md.Icono
		SET @Id = @Id + 1	
		FETCH NEXT FROM InsIncome
		INTO @SerialId
		SET @Cur = @Cur + 1
		END
		CLOSE InsIncome
	DEALLOCATE InsIncome

	SELECT * FROM #TempMovs

END

DROP TABLE #TempCiclo
DROP TABLE #TempMovs
--SELECT DISTINCT min(convert(date,fecha,101))FechaInicial,max(convert(date,fecha,101))FechaFinal,SerialId 
--INTO #TempFecha
--FROM Movimiento 
--WHERE SerialId=@SerialId
--GROUP BY SerialId
END




go


CREATE PROCEDURE [dbo].[pa_GetUnreadableReceiptByPallet]
	@Pallet nvarchar(20) = null
as
/*
exec pa_GetUnreadableReceiptByPallet '111'
*/
if(@Pallet is null)
begin
	select p.Id, p.Numero, cs.Codigo, cs.Descripcion, COUNT(i.serial) as Cantidad 
	from Pallet p
	inner join Ingreso i on p.id=i.palletId
	inner join CodigoSap cs on cs.id=p.codigoSapId
	inner join WmsWdGeneral.dbo.Estado d on p.destinoId=d.id
	where d.nombre='P-CONFIRMACION' and p.activo=1
	group by p.id,p.numero,cs.codigo,cs.descripcion
end
else
begin
	select p.Id, p.Numero, cs.Codigo, cs.Descripcion, COUNT(i.serial) as Cantidad  
	from Pallet p 
	inner join Ingreso i on p.id=i.palletId
	inner join CodigoSap cs on cs.id=p.codigoSapId
	inner join WmsWdGeneral.dbo.Estado d on p.destinoId=d.id
	where p.numero= @Pallet and d.nombre='P-CONFIRMACION' and p.activo=1
	group by p.id,p.numero,cs.codigo,cs.descripcion
end




go

CREATE PROCEDURE [dbo].[pa_GetValidaStateInventory]
		@Estado varchar(20),
		@PadreInventario varchar(20)
		AS
		BEGIN

			SET NOCOUNT ON;

			SELECT Nombre FROM Estado
			WHERE Nombre = @Estado AND PadreInventario = @PadreInventario
		END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_GetWarrantyMaster]
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	SELECT m.codigo FROM Maestro m 
	INNER JOIN TipoMaestro tm ON m.tipoMaestroId=tm.id 
	WHERE tm.nombre='Garantia'

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InactivateAllBoxDispatch]
@CajaDespachoId INT,
@Filas int output
AS
/*
DECLARE @CajaDespachoId INT
SET @CajaDespachoId = 0
EXEC pa_GetCountBoxDispatch @CajaDespachoId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    UPDATE CajaDespacho SET activo=0 
	WHERE palletId=@CajaDespachoId
	select @Filas=@@ROWCOUNT
END




go

CREATE PROCEDURE [dbo].[pa_InactivateAllBoxEntry]
        @PalletId INT,
        @Filas int output
        AS
        BEGIN

            UPDATE CajaIngreso SET activo=0 
            WHERE palletId=@PalletId
            select @Filas=@@ROWCOUNT
        END
go


CREATE PROCEDURE [dbo].[pa_InactivateBoxDispatch]
@CajaDespachoId INT
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    UPDATE CajaDespacho SET activo=0 
	WHERE id=@CajaDespachoId
END

go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InactivateBoxPacking]
@CajaEmpaqueId INT,
@Filas int output
AS
/*
DECLARE @CajaEmpaqueId INT
SET @CajaEmpaqueId=0
EXEC pa_InactivateBoxPacking @CajaEmpaqueId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    UPDATE CajaEmpaque 
    SET activo=0 
	WHERE id=@CajaEmpaqueId
	select @Filas=@@ROWCOUNT
END




go

CREATE   PROCEDURE [dbo].[pa_InformeDespachoTLCO2]
@FechaInicial varchar(20),
@FechaFinal varchar(20),
@cliente varchar(50),
@pais varchar(50)	
AS
BEGIN
	SET NOCOUNT ON;
	
	WITH CTE_TempG2 AS
	  (SELECT m.serial,
			  m.serialid,
			  'G2' AS nivel
	   FROM WmsWdTelefonicaColombiaBOG.dbo.Movimiento m
	   WHERE m.origenid = 22),
		 CTE_TempG3 AS
	  (SELECT r.serial,
			  r.serialid,
			  'G3' AS nivel
	   FROM WmsWdTelefonicaColombiaBOG.dbo.Reparacion r),
		 CTE_TempG4 AS
	  (SELECT m.serial,
			  m.serialid,
			  'G4' AS nivel
	   FROM WmsWdTelefonicaColombiaBOG.dbo.Movimiento m
	   WHERE destinoid = 20),
		 CTE_Tecnologia AS
	  (SELECT cs.id,
			  cs.codigo,
			  cs.Descripcion,
			  CASE
				WHEN m.descripcion LIKE '%BASEPORT%' THEN 'BP'
				WHEN m.descripcion LIKE '%DECO HD%' THEN 'DECO HD'
				WHEN m.descripcion LIKE '%IPTV%' OR m.descripcion LIKE '%SKYWORTH%' THEN 'IPTV'
				WHEN m.descripcion LIKE '%HGU%' OR m.descripcion LIKE '%WIFI 6%' THEN 'HGU'
				WHEN m.descripcion LIKE '%VDSL%' THEN 'VDSL'
				ELSE 'OTROS'
			END AS TipoEquipo,
			m.codigo as Familia
	   FROM WmsWdTelefonicaColombiaBOG.dbo.CodigoSap cs
	   INNER JOIN WmsWdTelefonicaColombiaBOG.dbo.Maestro m ON m.id = cs.familiaId)
	SELECT DISTINCT e.Serial,
					e.Mac,
					tpg.Codigo as Tipologia,
					t.Codigo as CodigoSap,
					t.Descripcion,
					p.Numero as Pallet,
					u.nombres + ' ' + u.apellidos as NombreUsuario,
					e.pedidosap as Orden,
					t.Familia,					
					t.tipoEquipo AS Tecnologia,
					CONCAT(t.tipoEquipo, 'G0') AS G0,
					CONCAT(t.tipoEquipo, 'G1') AS G1,
					CASE
						WHEN tpg2.nivel IS NOT NULL THEN CONCAT(t.tipoEquipo, tpg2.nivel)
					END AS G2,
					CASE
						WHEN tpg3.nivel IS NOT NULL THEN CONCAT(t.tipoEquipo, tpg3.nivel)
					END AS G3,
					CASE
						WHEN tpg4.nivel IS NOT NULL THEN CONCAT(t.tipoEquipo, tpg4.nivel)
					END AS G4,
					CONCAT(t.tipoEquipo, 'G5') AS G5
	FROM WmsWdTelefonicaColombiaBOG.dbo.Despacho e
	LEFT JOIN CTE_TempG2 tpg2 ON tpg2.SerialId = e.SerialId
	LEFT JOIN CTE_TempG3 tpg3 ON tpg3.SerialId = e.SerialId
	LEFT JOIN CTE_TempG4 tpg4 ON tpg4.SerialId = e.SerialId
	INNER JOIN CTE_Tecnologia t ON t.id = e.CodigoSapId
	LEFT JOIN WmsWdTelefonicaColombiaBOG.dbo.Pallet p on e.PalletId = p.Id
	LEFT JOIN WmsWdTelefonicaColombiaBOG.dbo.Usuario u on e.UsuarioId = u.Id
	LEFT JOIN WmsWdTelefonicaColombiaBOG.dbo.Maestro tpg on e.TipologiaId = tpg.Id
	LEFT JOIN WmsWdTelefonicaColombiaBOG.dbo.Maestro ti on e.TipoOrigenId = ti.Id
	LEFT JOIN WmsWdTelefonicaColombiaBOG.dbo.Maestro o on e.OrigenId = o.Id 
	WHERE CONVERT(date,e.Fecha) BETWEEN @FechaInicial AND @FechaFinal;
END
go

-- =============================================
-- Author:		Andrés Pulido
-- Create date: 26/12/2022
-- Description:	Reporte reversa TBL_REVERSE_LAB_PROCESSED
-- =============================================
Create PROCEDURE [dbo].[pa_InformeMillicomReverseLabProcessed]
AS
BEGIN
	-- EL SALVADOR
	-- Serializado empaque
	select 'EL SALVADOR' AS 'COUNTRY', convert(date,i.FechaMovimiento) AS 'PROCESSED_DATE',cs.Codigo AS 'MODEL_CODE', REPLACE(cs.Descripcion, '|', ' ') AS 'MODEL_DESCRIPTION',f.Codigo AS 'CPE_CATEGORY',
	o.Codigo AS 'DX_TYPE', REPLACE(i.Serial, '|', ' ') AS 'SERIAL_1', REPLACE(i.mac, '|', ' ') AS 'SERIAL_2', CASE WHEN i.Serial3 IS NULL THEN '' ELSE REPLACE(i.Serial3, '|', ' ') END As 'SERIAL_3', e.Nombre AS 'LAB_PHASE'
	-- TABLAS
	INTO #Salvador
	from Ingreso i  
	left join CodigoSap cs  on i.CodigoSapId=cs.id 
	left join Maestro f  on cs.FamiliaId=f.Id 
	left join Pallet p  on i.PalletId=p.id 
	left join Posicion po  on p.PosicionId=po.id 
	left join Maestro o  on i.OrigenId=o.id  
	left join Estado e on i.EstadoId=e.id 
	left join(select serial,sum(cantidad)TotalIngresos from 
	(select serial,count(serial)cantidad from Ingreso  group by serial union all 
	select serial,count(serial)cantidad from Despacho  group by serial)as tabla group by serial)as tabla2 on i.Serial=tabla2.Serial

	SELECT * FROM #Salvador

	ORDER BY COUNTRY, PROCESSED_DATE ASC

	DROP TABLE #Salvador
END



go

-- =============================================
-- Author:		Andrés Pulido
-- Create date: 14/06/2023
-- Description: TBL_REVERSE_LAB_Produced
-- =============================================
CREATE PROCEDURE [dbo].[pa_InformeMillicomReverseLabProduced]
AS
BEGIN
	--EL SALVADOR
	--Serializado empaque
	select 'EL SALVADOR' as 'COUNTRY', convert(date,e.Fecha)as 'PRODUCTION_DATE',cs.Codigo as 'MODEL_CODE', REPLACE(cs.Descripcion, '|', ' ') as 'MODEL_DESCRIPTION', f.Codigo as 'CPE_CATEGORY', CONVERT(varchar(50),'') as 'DX_TYPE', REPLACE(e.serial, '|', ' ') as 'SERIAL_1', REPLACE(e.Mac, '|', ' ') AS 'SERIAL_2', CASE WHEN ISNULL(i.Serial3, '')='' THEN REPLACE(i.Serial3, '|', ' ') END AS 'SERIAL_3',
	convert(varchar(20),concat('NIVEL ',convert(int,n.Codigo)-1)) as 'REPAIR_LEVEL',convert(varchar(100),'')as 'DIAGNOSTIC',
	ISNULL(t.precio,0) as 'REPAIR_COST', 'LC, USD' AS 'CURRENCY'
	into #EmpaqueSal
	from WmsWdTigoSal.dbo.Empaque e
	left join WmsWdTigoSal.dbo.Maestro n on e.NivelId = n.Id 
	inner join WmsWdTigoSal.dbo.CodigoSap cs on e.CodigoSapId=cs.Id 
	inner join WmsWdTigoSal.dbo.Maestro f on cs.FamiliaId=f.Id 
	left join WmsWdTigoSal.dbo.Tarifa t on e.NivelId=t.nivelId
	left join WmsWdTigoSal.dbo.Ingreso i on e.Serial=i.Serial
	where e.Fecha between CONVERT(datetime, '2023-01-07 00:00:00.001') and CONVERT(datetime,'2023-15-07 23:59:59.001')--(SELECT DATEADD(DD, -1, GETDATE()))
	ORDER BY e.Fecha ASC
	--Serializado de scrap
	select 'EL SALVADOR' as 'COUNTRY', convert(date,r.FechaReparacion)as 'PRODUCTION_DATE',cs.Codigo as 'MODEL_CODE', REPLACE(cs.Descripcion, '|', ' ') as 'MODEL_DESCRIPTION', f.Codigo as 'CPE_CATEGORY', case when ISNULL(oi.Codigo,'')='' then od.Codigo else oi.Codigo end as 'DX_TYPE',REPLACE(r.serial, '|', ' ') as 'SERIAL_1', REPLACE(r.Mac, '|', ' ') AS 'SERIAL_2', CASE WHEN ISNULL(i.Serial3, '')='' THEN REPLACE(i.Serial3, '|', ' ') END AS 'SERIAL_3',
	'SCRAP' as 'REPAIR_LEVEL',ms.Descripcion as 'DIAGNOSTIC',
	case when ISNULL(ti.precio,'0')='0' then td.precio else ti.precio end as 'REPAIR_COST', 'LC, USD' AS 'CURRENCY'
	into #SerializadoScrapSal
	from WmsWdTigoSal.dbo.Reparacion r 
	inner join WmsWdTigoSal.dbo.CodigoSap cs on r.CodigoSapId=cs.Id 
	inner join WmsWdTigoSal.dbo.Maestro ms on r.MotivoScrapId=ms.Id 
	inner join WmsWdTigoSal.dbo.Maestro f on cs.FamiliaId=f.Id 
	left join WmsWdTigoSal.dbo.Ingreso i on r.SerialId=i.Id 
	left join WmsWdTigoSal.dbo.Maestro oi on i.OrigenId=oi.Id 
	left join WmsWdTigoSal.dbo.Tarifa ti on i.NivelId=ti.nivelId 
	left join WmsWdTigoSal.dbo.Despacho d on r.SerialId=d.SerialId 
	left join WmsWdTigoSal.dbo.Maestro od on d.OrigenId=od.Id 
	left join WmsWdTigoSal.dbo.Tarifa td on d.NivelId=td.nivelId
	left join WmsWdTigoSal.dbo.Ingreso ing on r.Serial=ing.Serial
	where r.FechaReparacion between CONVERT(datetime, '2023-01-07 00:00:00.001') and CONVERT(datetime,'2023-15-07 23:59:59.001')--(SELECT DATEADD(DD, -1, GETDATE()))
	ORDER BY r.FechaReparacion ASC
	--Se actualiza el origen según ingreso o despacha
	update #EmpaqueSal
	set DX_TYPE = isnull(m.Codigo,md.Codigo)
	from #EmpaqueSal e
	left join WmsWdTigoSal.dbo.Ingreso i on e.[SERIAL_1] = i.Serial
	left join WmsWdTigoSal.dbo.Maestro m on i.OrigenId = m.Id 
	left join WmsWdTigoSal.dbo.Despacho d on e.[SERIAL_1] = d.Serial
	left join WmsWdTigoSal.dbo.Maestro md on d.OrigenId = md.Id 
	--se obtinen la falla de diagnostico
	select e.[SERIAL_1],MAX(Fecha)as fecha,fd.Codigo, --fd.Codigo+': '+fd.Descripcion as Falla 
	CASE WHEN (fd.Codigo='') OR (fd.Codigo=NULL) THEN 'NO ENCIENDE' ELSE fd.Descripcion END AS Falla
	into #DiagnosticoSal from #EmpaqueSal e 
	inner join WmsWdTigoSal.dbo.Diagnostico d on e.[SERIAL_1]=d.Serial
	inner join WmsWdTigoSal.dbo.Maestro fd on d.FallaId=fd.Id 
	where e.[REPAIR_LEVEL] in('NIVEL 0','NIVEL 1') and d.Fecha=(select max(fecha) from WmsWdTigoSal.dbo.Diagnostico where Serial=e.[SERIAL_1])
	group by e.[SERIAL_1],fd.Codigo,fd.Descripcion 
	order by e.[SERIAL_1]
	--se actualiza la falla de diagnostico
	update #EmpaqueSal
	set DIAGNOSTIC =d.Falla 
	from #EmpaqueSal e 
	inner join #DiagnosticoSal d on e.[SERIAL_1]=d.[SERIAL_1]
	--se obtiene falla de reparacion
	select e.[SERIAL_1],MAX(r.FechaReparacion)as fecha, 
	CASE WHEN (e.[REPAIR_LEVEL]='NIVEL 2') 
		THEN 
		CASE WHEN (fr.Codigo='SF') 
			THEN 'CAIDA DE SEÑAL' 
			ELSE fr.Descripcion END 
		ELSE 
		CASE WHEN (fr.Descripcion='') OR (fr.Descripcion=NULL)
			THEN 'NO ENCIENDE' 
			ELSE fr.Descripcion END 
	END as Falla 
	into #ReparacionSal from #EmpaqueSal e 
	inner join WmsWdTigoSal.dbo.Reparacion r on e.[SERIAL_1]=r.Serial
	inner join WmsWdTigoSal.dbo.Maestro fr on r.Falla1Id=fr.Id 
	where e.[REPAIR_LEVEL] ='NIVEL 2' and r.FechaReparacion=(select max(FechaReparacion) from WmsWdTigoSal.dbo.Reparacion where Serial=e.[SERIAL_1])
	group by e.[SERIAL_1],e.[REPAIR_LEVEL],fr.Codigo,fr.Descripcion 
	order by e.[SERIAL_1]
	--se actualiza la falla de reparacion
	update #EmpaqueSal
	set DIAGNOSTIC =r.Falla 
	from #EmpaqueSal e 
	inner join #ReparacionSal r on e.[SERIAL_1]=r.[SERIAL_1]
	update #EmpaqueSal set DIAGNOSTIC='CAIDA DE SEÑAL' where DIAGNOSTIC='' AND REPAIR_LEVEL='NIVEL 2'
	update #EmpaqueSal set DIAGNOSTIC='SIN FALLA' where DIAGNOSTIC='' AND (REPAIR_LEVEL='NIVEL 0' OR REPAIR_LEVEL='NIVEL 1')
	update #EmpaqueSal set SERIAL_2='' where SERIAL_2 IS NULL
	update #EmpaqueSal set SERIAL_3='' where SERIAL_3 IS NULL
	update #SerializadoScrapSal set SERIAL_2='' where SERIAL_2 IS NULL
	update #SerializadoScrapSal set SERIAL_3='' where SERIAL_3 IS NULL
	update #EmpaqueSal set DIAGNOSTIC='FALLA HDMI' WHERE DIAGNOSTIC LIKE '%HDMI%'
	update #SerializadoScrapSal set DIAGNOSTIC='FALLA HDMI' WHERE DIAGNOSTIC LIKE '%HDMI%'
	update #EmpaqueSal set DIAGNOSTIC='FALLA WIFI' WHERE DIAGNOSTIC LIKE '%WIFI%'
	update #SerializadoScrapSal set DIAGNOSTIC='FALLA WIFI' WHERE DIAGNOSTIC LIKE '%WIFI%'
	update #EmpaqueSal set DIAGNOSTIC='DOBLE COMPONENTE' WHERE DIAGNOSTIC LIKE '%COMPONENTE%'
	update #SerializadoScrapSal set DIAGNOSTIC='DOBLE COMPONENTE' WHERE DIAGNOSTIC LIKE '%COMPONENTE%'
	update #EmpaqueSal set DIAGNOSTIC='CAIDA DE SEÑAL' WHERE DIAGNOSTIC LIKE '%SEÑAL%'
	update #SerializadoScrapSal set DIAGNOSTIC='CAIDA DE SEÑAL' WHERE DIAGNOSTIC LIKE '%SEÑAL%'
	update #EmpaqueSal set DIAGNOSTIC='RECEPCION REMOTA' WHERE DIAGNOSTIC LIKE '%REMOTA%'
	update #SerializadoScrapSal set DIAGNOSTIC='RECEPCION REMOTA' WHERE DIAGNOSTIC LIKE '%REMOTA%'

	select * from #EmpaqueSal union all select * from #SerializadoScrapSal

	ORDER BY COUNTRY, PRODUCTION_DATE

	drop table #EmpaqueSal
	drop table #SerializadoScrapSal
	drop table #DiagnosticoSal
	drop table #ReparacionSal
END
go

CREATE PROCEDURE dbo.pa_InformeWipTelefonica
AS 
BEGIN 
	  select 
	  i.Serial,
	  cs.Codigo as codigosap,
	  cs.Descripcion,
	  fa.Codigo as familia,
	  c.Codigo as tipologia,		  
	  isnull(e.nombre, '') as localizacion,
	  isnull(p.Numero, '') as pallet, 
	  isnull(po.Numero, '') as posicion,
	  n.Codigo as nivel,
	   convert(
	    date, 
	    dateadd(hour,-6, i.Fecha)
	  ) as 'FECHAINGRESO', 
	  convert(
	    date, 
	    dateadd(hour,-6, i.FechaMovimiento)
	  ) as 'FECHAMOVIMIENTO',
	  tor.Codigo as tipoOrigen,
	  o.Codigo as origen,
	  isnull(op.Nombre, '') as origenPallet,
	  i.GarantiaFabricante, 
	  t.Codigo as estado,
	  isnull(TotalIngresos, 0) as TotalIngresos,		  
	  ar.Codigo as Area,		    
	  te.Codigo as TipodeEquipo	  
	from 
	  WmsWdTelefonicaColombiaBOG.dbo.Ingreso i with(nolock) 
	  left join WmsWdTelefonicaColombiaBOG.dbo.CodigoSap cs with(nolock) on i.CodigoSapId = cs.id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Maestro fa with(nolock) on cs.familiaid = fa.Id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Maestro te with(nolock) on cs.tipoequipoid = te.id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Maestro ar with(nolock) on cs.areaid = ar.id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Maestro c with(nolock) on cs.ClasificacionId = c.id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Estado e with(nolock) on i.EstadoId = e.id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Pallet p with(nolock) on i.PalletId = p.id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Posicion po with(nolock) on p.PosicionId = po.id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Maestro n with(nolock) on i.NivelId = n.id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Maestro tor with(nolock) on i.TipoOrigenId = tor.id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Maestro o with(nolock) on i.OrigenId = o.id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Estado op with(nolock) on p.OrigenId = op.id 
	  left join WmsWdTelefonicaColombiaBOG.dbo.Maestro t with(nolock) on i.TipologiaId = t.id 
	  left join (
	    select 
	      serial, 
	      sum(cantidad) TotalIngresos 
	    from 
	 (
	        select 
	          serial, 
	          count(serial) cantidad 
	        from 
	          WmsWdTelefonicaColombiaBOG.dbo.Ingreso with(nolock) 
	        group by 
	          serial 
	        union all 
	        select 
	          serial, 
	          count(serial) cantidad 
	        from 
	          WmsWdTelefonicaColombiaBOG.dbo.Despacho with(nolock) 
	        group by 
	          serial
	      ) as tabla 
	    group by 
	      serial
	  ) as tabla2 on i.Serial = tabla2.Serial
END
go

CREATE PROCEDURE [dbo].[pa_InnactivateCiudad]
            @Id int,
            @Estado int
            AS

            BEGIN
                -- SET NOCOUNT ON added to prevent extra result sets from
                -- interfering with SELECT statements.

            UPDATE Ciudad SET activo=@Estado 
            WHERE id=@Id
            
            END
go

CREATE PROCEDURE [dbo].[pa_InnactivateLote]
            @Id int,
            @Estado int
            AS

            BEGIN
                -- SET NOCOUNT ON added to prevent extra result sets from
                -- interfering with SELECT statements.

            UPDATE Lote SET activo=@Estado 
            WHERE id=@Id
            
            END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InnactivateMaster]
@Estado int,
@Id int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Maestro SET Activo=@Estado 
	WHERE Id=@Id
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InnactivateMasterType]
@TipoMaestroId int,
@Estado int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	update TipoMaestro set activo=@Estado where id=@TipoMaestroId
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InnactivatePallet]
@PalletId int,
@Filas int output
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    update Pallet set activo=0 
	where id=@PalletId
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InnactivatePrealert]
@Id int,
@Estado int,
@Filas int output
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    update Prealerta set activo=@Estado where id=@Id
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InnactivateSapCode]
@Id INT,
@Estado INT,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_InnactivateSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE CodigoSap SET Activo=@Estado 
	WHERE Id=@Id
	select @Filas=@@ROWCOUNT
END




go

CREATE PROCEDURE [dbo].[pa_InnactivateSapCodeFailure]
        @CodigoSapId INT,
        @FallaId INT,
        @Estado INT,
        @Filas int output
        AS

        BEGIN

            UPDATE CodigoSapFalla SET ComponenteObligatorio=@Estado 
            WHERE CodigoSapId=@CodigoSapId AND FallaId = @FallaId
            select @Filas=@@ROWCOUNT
        END
go

CREATE PROCEDURE [dbo].[pa_InnactivateTransportadora]
            @Id int,
            @Estado int
            AS

            BEGIN
                -- SET NOCOUNT ON added to prevent extra result sets from
                -- interfering with SELECT statements.

            UPDATE Transportadora SET activo=@Estado 
            WHERE id=@Id
            
            END
go


CREATE PROCEDURE [dbo].[pa_InsertAccCodigoSap]
@codigoSapId int,
@accesorioId int
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	BEGIN TRY
		BEGIN TRANSACTION
			insert into CodigoSapAccesorio(codigosapId,AccesorioId)values(@codigoSapId,@accesorioId)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END
go

CREATE PROCEDURE [dbo].[pa_InsertAccesory]
@CodigoSapId int,
@TipoAccesorio varchar(20),
@TipoOrigenId int,
@OrigenId int,
@PalletId int,
@EstadoId int,
@Documento varchar(50),
@Observacion nvarchar(250),
@Guia varchar(50),
@UsuarioId int,
@Caja int,
@PrealertaId INT,
@Cruce BIT
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	BEGIN TRY
		BEGIN TRANSACTION
			INSERT INTO Accesorio(CodigoSapId,TipoAccesorio,TipoOrigenId,OrigenId,PalletId,EstadoId,Documento,Observacion,Guia,UsuarioId,Fecha,Caja,PrealertaId,Cruce)
			VALUES(@CodigoSapId,@TipoAccesorio,@TipoOrigenId,@OrigenId,@PalletId,@EstadoId,@Documento,@Observacion,@Guia,@UsuarioId,GETDATE(),@Caja,@PrealertaId,@Cruce)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH;

END
go

CREATE   PROCEDURE [dbo].[pa_InsertAssemble]
    @SerialId INT,
    @Serial VARCHAR(50),
    @Mac VARCHAR(50),
    @Serial3 VARCHAR(50),
    @Serial4 VARCHAR(50),
    @Serial5 VARCHAR(50),
    @CodigoSapId INT,
    @PalletId INT,
    @Estadoid INT,
    @TipologiaId INT,
    @NivelId INT,
    @UsuarioId INT,
    @LoteId INT,
    @SmartCard VARCHAR(50),
	@UsuarioIdAsignado INT
AS
BEGIN
    --SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRANSACTION;        
            INSERT INTO [dbo].[Ensamble] (
                SerialId,
                Serial,
                Mac,
                Serial3,
                Serial4,
                Serial5,
                CodigoSapId,
                PalletId,
                Estadoid,
                TipologiaId,
                NivelId,
                UsuarioId,
				UsuarioIdAsignado,
                Fecha,
                LoteId,
                SmartCard
            )
            VALUES (
                @SerialId,
                @Serial,
                @Mac,
                @Serial3,
                @Serial4,
                @Serial5,
                @CodigoSapId,
                @PalletId,
                @Estadoid,
                @TipologiaId,
                @NivelId,
                @UsuarioId,
				@UsuarioIdAsignado,
                GETDATE(), 
                @LoteId,
                @SmartCard
            );     
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
    END CATCH;
END;
go


CREATE PROCEDURE [dbo].[pa_InsertBoxDispatch]
@Numero VARCHAR(50),
@PalletId INT,
@UsuarioId INT,
@Fecha DATETIME
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_InsertBoxDispatch @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	BEGIN TRY
		INSERT INTO CajaDespacho(Numero,PalletId,UsuarioId,Fecha,Activo)
		VALUES(@Numero,@PalletId,@UsuarioId,@Fecha,1)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH;

END
go


CREATE PROCEDURE [dbo].[pa_InsertBoxEntry]
        @Numero VARCHAR(50),
        @PalletId INT,
        @UsuarioId INT,
        @Fecha DATETIME
        AS
        BEGIN
            
            BEGIN TRY
                INSERT INTO CajaIngreso(Numero,PalletId,UsuarioId,Fecha,Activo)
                VALUES(@Numero,@PalletId,@UsuarioId,@Fecha,1)
                COMMIT TRANSACTION
            END TRY
            BEGIN CATCH
                ROLLBACK TRANSACTION
            END CATCH;

        END
go


CREATE PROCEDURE [dbo].[pa_InsertBoxPacking]
@Numero VARCHAR(50),
@PalletId INT,
@EstadoId INT,
@UsuarioId INT,
@Fecha DATETIME
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_InsertBoxPacking @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	BEGIN TRY
		BEGIN TRANSACTION
		INSERT INTO CajaEmpaque(Numero,PalletId,EstadoId,UsuarioId,Fecha,Activo)
		VALUES(@Numero,@PalletId,@EstadoId,@UsuarioId,@Fecha,1)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH;

END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InsertCiclic]
@Nombre VARCHAR(50),
@UsuarioId INT,
@Estado VARCHAR(15)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_InsertCiclic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	BEGIN TRY
		BEGIN TRANSACTION
			INSERT INTO Ciclico(Nombre,UsuarioId,Estado)
			VALUES(@Nombre,@UsuarioId,@Estado)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH;

END




go

CREATE PROCEDURE [dbo].[pa_InsertCiudad]
        @Nombre varchar(50),
        @CodigoCiudad varchar(120)
        AS
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.
            BEGIN TRY
                BEGIN TRANSACTION	
                    INSERT INTO Ciudad(nombre,codigoCiudad,activo)
                    VALUES(@Nombre,@CodigoCiudad,1)
                COMMIT TRANSACTION
            END TRY
            BEGIN CATCH
                ROLLBACK TRANSACTION
            END CATCH
        END
go


CREATE PROCEDURE [dbo].[pa_InsertClasification]
@Serial VARCHAR(50),
@EstadoEnviado VARCHAR(30),
@UsuarioId INT,
@Fecha DATETIME,
@UsuarioAsignadoId INT
AS
BEGIN

	SET NOCOUNT ON;

	INSERT INTO Clasificacion(serialId,serial,mac, serial3, serial4, serial5, codigoSapId, nivelId, loteId, estadoEnviado, usuarioId, fecha, UsuarioAsignadoId)
	SELECT Id,Serial,Mac,
						Serial3,
						Serial4,
						Serial5,
						codigoSapId,
						nivelId,
						loteId,
						@EstadoEnviado,
						@UsuarioId,
						@Fecha,
						@UsuarioAsignadoId
	FROM Ingreso
	WHERE Serial = @Serial		
END
go

---------------------------------------------------------------------------------------------------------


CREATE   PROCEDURE [dbo].[pa_InsertClasificationWeb]
@Serial VARCHAR(50),
@UsuarioId INT,
@UsuarioAsignadoId INT
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO Clasificacion(serialId,serial,mac,serial3,serial4,serial5,codigoSapId,nivelId,estadoEnviado,loteId,usuarioId,fecha,UsuarioAsignadoId)
    SELECT Id,Serial,Mac,Serial3,Serial4,Serial5,codigoSapId,0,0,loteId,@UsuarioId,GETDATE(),@UsuarioAsignadoId
    FROM Ingreso 
    WHERE Serial=@Serial;
END
go


CREATE   PROCEDURE [dbo].[pa_InsertCleaning]
    @SerialId INT,
    @Serial VARCHAR(50),
    @Mac VARCHAR(50),
    @Serial3 VARCHAR(50),
    @CodigoSapId INT,
    @UsuarioId INT,
    @UsuarioAsignadoId INT
AS
BEGIN
    BEGIN TRY
        BEGIN TRANSACTION;
            INSERT INTO [dbo].[Limpieza] (
                SerialId,
                Serial,
                Mac,
                Serial3,
                CodigoSapId,
                UsuarioId,
                UsuarioAsignadoId,
                Fecha
            )
            VALUES (
                @SerialId,
                @Serial,
                @Mac,
                @Serial3,
                @CodigoSapId,
                @UsuarioId,
                @UsuarioAsignadoId,
                GETDATE()
            );
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
go

 
CREATE   PROCEDURE [dbo].[pa_InsertCodigoSapFalla]
    @codigoSapId INT,
    @fallaId INT
AS
BEGIN
    SET NOCOUNT ON;
 
    BEGIN TRY
        BEGIN TRANSACTION;
            INSERT INTO CodigoSapFalla (CodigoSapId, FallaId)
            VALUES (@codigoSapId, @fallaId);
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
            ROLLBACK TRANSACTION;        
        THROW;
    END CATCH
END
go

CREATE PROCEDURE [dbo].[pa_InsertComponentsAsig]
    @Serial VARCHAR(50),
    @ComponenteId INT
    AS
    BEGIN
        INSERT INTO SerialComponente(SerialId,Serial,ComponenteId)VALUES((SELECT id FROM Ingreso WHERE Serial = @Serial),@Serial,@ComponenteId)

    END
go

CREATE PROCEDURE [dbo].[pa_InsertCosmetica]
        @Serial VARCHAR(50),
		@UsuarioId INT,
        @Fecha DATETIME
		AS
		BEGIN

			SET NOCOUNT ON;

			INSERT INTO Cosmetica(serialId,serial,mac,serial3,serial4,serial5,codigoSapId,palletId,nivelId,loteId,usuarioId,fecha)
            SELECT Id,Serial,Mac,Serial3,Serial4,Serial5,codigoSapId,palletId,nivelId,loteId,@UsuarioId,@Fecha FROM Ingreso
            WHERE Serial =@Serial			
		END
go

/**
 * Author: Pedro Neira
 * Created: JUN, 17  2025
 */

CREATE PROCEDURE [dbo].[pa_InsertDiagnostic]
@SerialId INT,
@Serial VARCHAR(50),
@Mac VARCHAR(50),
@CodigoSapId INT,
@Variable1 VARCHAR(50),
@Variable2 VARCHAR(50),
@Variable3 VARCHAR(50),
@Variable4 VARCHAR(50),
@UsuarioId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	BEGIN TRY
		BEGIN TRANSACTION
			INSERT INTO Diagnostico(serialId,serial,mac,codigoSapId,variable1,variable2,variable3,variable4,EstadoFinalId,fallaId,usuarioId,fecha)
			VALUES(@SerialId,@Serial,@Mac,@CodigoSapId,@Variable1,@Variable2,@Variable3,@Variable4,0,0,@UsuarioId,GETDATE())
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH;

END
go


CREATE PROCEDURE [dbo].[pa_InsertDiagnosticWeb]
@SerialId INT,
@Serial VARCHAR(50),
@Mac VARCHAR(50),
@CodigoSapId INT,
@UsuarioId INT
AS
/*
DECLARE @PalletId INT
"
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	BEGIN TRY
		BEGIN TRANSACTION
		IF @CodigoSapId=50
		BEGIN
			INSERT INTO Diagnostico(serialId,serial,mac,codigoSapId,EstadoFinalId,fallaId,usuarioId,fecha)
			VALUES(@SerialId,SUBSTRING(@Serial,14,12),@Mac,@CodigoSapId,0,0,@UsuarioId,GETDATE())
		END
		ELSE 
			INSERT INTO Diagnostico(serialId,serial,mac,codigoSapId,EstadoFinalId,fallaId,usuarioId,fecha)
			VALUES(@SerialId,@Serial,@Mac,@CodigoSapId,0,0,@UsuarioId,GETDATE())
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH;
END;
go




CREATE   PROCEDURE [dbo].[pa_InsertDispatch]
@SerialId INT,
@Serial VARCHAR(50),
@Mac VARCHAR(50),
@CodigoSapId INT,
@PalletId INT,
@PalletIdIngreso INT,
@CajaDespachoId INT,
@EstadoId INT,
@TipoOrigenId INT,
@OrigenId INT,
@TipologiaId INt,
@NivelId INT,
@Tramite VARCHAR(30),
@Documento VARCHAR(30),
@Guia VARCHAR(30),
@Falla VARCHAR(100),
@PrealertaId INT,
@Cruce INT,
@Novedad VARCHAR(20),
@UsuarioId INT,
@FechaIngreso DATETIME,
@PedidoSap varchar(100),
@SmartCardId INT,
@SmartCard VARCHAR(50),
@LoteId INT,
@Serial3 VARCHAR(50),
@CajaIngresoId INT,
@NumeroSmartcard VARCHAR(20),
@FallaCosmeticaId int,
@FallaFuncionalId int,
@Causa VARCHAR(100),
@Observaciones VARCHAR(500) = NULL
AS

BEGIN
	BEGIN TRY BEGIN TRANSACTION
	INSERT INTO Despacho(serialId,serial,mac, codigoSapId, palletId, palletIdIngreso, cajaDespachoId, estadoId, tipoOrigenId, origenId, tipologiaId, nivelId, tramite, documento, guia, falla, prealertaId, cruce, novedad, usuarioId, fechaIngreso, fecha, PedidoSap, smartCardId, SmartCard, loteId, Serial3, cajaIngresoId, numeroSmartcard, FallaCosmeticaId, FallaFuncionalId,Causa)
	VALUES(@SerialId,
		   @Serial,
		   @Mac,
		   @CodigoSapId,
		   @PalletId,
		   @PalletIdIngreso,
		   @CajaDespachoId,
		   @EstadoId,
		   @TipoOrigenId,
		   @OrigenId,
		   @TipologiaId,
		   @NivelId,
		   @Tramite,
		   @Documento,
		   @Guia,
		   @Falla,
		   @PrealertaId,
		   @Cruce,
		   @Novedad,
		   @UsuarioId,
		   @FechaIngreso,
		   GETDATE(),
		   @PedidoSap,
		   @SmartCardId,
		   @SmartCard,
		   @LoteId,
		   @Serial3,
		   @CajaIngresoId,
		   @NumeroSmartcard,
		   @FallaCosmeticaId,
		   @FallaFuncionalId,
		   @Causa)
	COMMIT TRANSACTION END TRY BEGIN CATCH
	ROLLBACK TRANSACTION END CATCH;

END
go


CREATE   PROCEDURE [dbo].[pa_InsertDispatchAccesory]
    @AccesoryId INT,
    @CodigoSapId INT,
    @TipoAccesorio VARCHAR(50),
    @TipoOrigenId INT,
    @OrigenId INT,
    @PalletId INT,
    @EstadoId INT,
    @EstadoLimpiezaId INT,
    @Documento VARCHAR(50),
    @Observacion VARCHAR(250),
    @Guia VARCHAR(50),
    @UsuarioId INT,
    @FechaIngreso DATETIME,
    @SerialEmpaque VARCHAR(50),
    @Caja INT,
    @PedidoSap VARCHAR(20),
    @FechaLimpieza DATETIME,
    @FechaEmpaque DATETIME,
    @UsuarioLimpiezaId INT
AS
BEGIN                
    BEGIN TRY
        BEGIN TRANSACTION;
            INSERT INTO DespachoAccesorio(AccesoryId, CodigoSapId, TipoAccesorio, TipoOrigenId, OrigenId, PalletId, EstadoId, EstadoLimpiezaId, Documento, Observacion, Guia, UsuarioId, FechaIngreso, SerialEmpaque, Caja, Fecha, PedidoSap, fechaLimpieza, fechaEmpaque, UsuarioLimpiezaId)
            VALUES(@AccesoryId, @CodigoSapId, @TipoAccesorio, @TipoOrigenId, @OrigenId, @PalletId, @EstadoId, @EstadoLimpiezaId, @Documento, @Observacion, @Guia, @UsuarioId, @FechaIngreso, @SerialEmpaque, @Caja, GETDATE(), @PedidoSap, @FechaLimpieza, @FechaEmpaque, @UsuarioLimpiezaId);
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
go


/**
 * Author:  Pedro Neira
 * Created: FEB, 23 2024
 */

CREATE PROCEDURE [dbo].[pa_InsertDispatchBoxFromPackedBox]
    @PalletId INT
    AS
    
    BEGIN
        INSERT INTO CajaDespacho(Numero,PalletId,usuarioId,Fecha,Activo)
        SELECT Numero,palletId,usuarioId,GETDATE(),Activo
        FROM CajaEmpaque Where PalletId =@PalletId

        UPDATE Ingreso
        SET cajaDespachoId = CajaDespacho.id
        FROM Ingreso i
        INNER JOIN CajaEmpaque ON i.cajaEmpaqueId = CajaEmpaque.id
        INNER JOIN CajaDespacho ON CajaEmpaque.numero = CajaDespacho.numero
        Where i.PalletId = @PalletId AND CajaDespacho.PalletId = @PalletId

        UPDATE Ingreso SET estadoId = (SELECT id FROM estado WHERE Nombre = 'PREDESPACHADO')
        WHERE PalletID = @PalletId
    
    END
go

CREATE PROCEDURE [dbo].[pa_InsertEntry]
@Serial varchar(50),
@Mac varchar(50),
@Serial3 varchar(50),
@Serial4 varchar(50),
@Serial5 varchar(50),
@CodigoSapId int,
@PalletId int,
@EstadoId int,
@TipoOrigenId int,
@OrigenId int,
@TipologiaId int,
@NivelId int,
@Tramite varchar(30),
@Documento varchar(30),
@Guia varchar(30),
@Caja int,
@Falla varchar(100),
@TecnicoCliente varchar(50),
@PrealertaId int,
@Cruce int,
@Novedad varchar(50),
@GarantiaFabricante int,
@UsuarioId int,
@Observaciones varchar(50),
@EstadoCliente varchar(20),
@LoteId int,
@CajaIngresoId int,
@ModeloId INT,
@codigoError INT = 0 OUTPUT,
@mensajeError NVARCHAR(4000) = N'' OUTPUT
AS
BEGIN
	BEGIN TRY
		BEGIN TRANSACTION
			INSERT INTO Ingreso(
				serial, mac, serial3, serial4, serial5, codigoSapId, palletId, palletIdIngreso,
				estadoId, tipoOrigenId, origenId, tipologiaId, nivelId, tramite, documento, guia,
				caja, falla, tecnicoCliente, prealertaId, cruce, novedad, garantiaFabricante,
				usuarioId, fecha, usuarioIdMovimiento, fechaMovimiento, observaciones,
				estadoCliente, loteId, cajaIngresoId, modeloId
			)
			VALUES (
				UPPER(@Serial), UPPER(@Mac), @Serial3, UPPER(@Serial4), UPPER(@Serial5),
				@CodigoSapId, @PalletId, @PalletId, @EstadoId, @TipoOrigenId, @OrigenId,
				@TipologiaId, @NivelId, @Tramite, @Documento, @Guia, @Caja, @Falla,
				@TecnicoCliente, @PrealertaId, @Cruce, @Novedad, @GarantiaFabricante,
				@UsuarioId, GETDATE(), @UsuarioId, GETDATE(), @Observaciones,
				@EstadoCliente, @LoteId, @CajaIngresoId, @ModeloId
			)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION;
			
		   SET @mensajeError = ERROR_MESSAGE();
			IF @mensajeError LIKE '%UNIQUE%' AND @mensajeError LIKE '%Serial%'
				--RAISERROR(-1, 16, 1);
				SET @codigoError = '1001'
			ELSE IF @mensajeError LIKE '%UNIQUE%' AND @mensajeError LIKE '%MAC%'
				SET @codigoError = '1002'
			ELSE
				SET @codigoError = '1003'
			RETURN;
		-- O si prefieres retornar info controladamente:
		-- SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	END CATCH
END
go

CREATE PROCEDURE [dbo].[pa_InsertEtiquetado]
    @Serial VARCHAR(50),
    @Mac VARCHAR(50),
    @Variable1 VARCHAR(50),
    @Variable2 VARCHAR(50),
    @Variable3 VARCHAR(50),
    @Variable4 VARCHAR(50),
    @Reimpresion BIT,
    @UsuarioId INT,
    @Fecha DATETIME
    AS
    BEGIN
        SET NOCOUNT ON;

        DECLARE @SerialId INT, @CodigoSapId VARCHAR(50), @NivelId INT, @LoteId INT,@EstadoId INT;

        SELECT @SerialId = id, @CodigoSapId = codigoSapId, @NivelId = nivelId, @LoteId = loteId, @EstadoId = estadoId
        FROM Ingreso
        WHERE Serial = @Serial;

        IF @SerialId IS NOT NULL
        BEGIN
            INSERT INTO Etiquetado(serialId,serial,mac,codigoSapId,nivelId,loteId,variable1,variable2,variable3,variable4,estadoId,reimpresion,usuarioId,fecha)
            VALUES (@SerialId, @Serial, @Mac, @CodigoSapId, @NivelId, @LoteId, @Variable1, @Variable2, @Variable3, @Variable4, @EstadoId,@Reimpresion, @UsuarioId, @Fecha);
        END
        
    END
go

CREATE PROCEDURE [dbo].[pa_InsertForecast] @linea_negocio NVARCHAR(255) = NULL, @fecha DATE, @fecha_entrega DATE = NULL, @forecast_und DECIMAL(18,2) = 0, @jornada NVARCHAR(8) = 'LV', @dias_habiles INT = NULL, @observaciones NVARCHAR(500) = NULL, @usuario NVARCHAR(150) = NULL, @familias_json NVARCHAR(MAX) = NULL AS BEGIN SET NOCOUNT ON; INSERT INTO Forecast (linea_negocio, fecha, fecha_entrega, forecast_und, jornada, dias_habiles, observaciones, usuario, familias_json) VALUES (@linea_negocio, @fecha, @fecha_entrega, @forecast_und, @jornada, @dias_habiles, @observaciones, @usuario, @familias_json); END;
go

CREATE PROCEDURE [dbo].[pa_InsertInventarioFaltante]
		@UsuarioId INT,
		@Documento VARCHAR(50),
		@Filas int output
		AS
		BEGIN

			SET NOCOUNT ON;

			INSERT INTO InventarioFaltante  (
				SerialId, Serial, Mac, Serial3, Serial4, Serial5, SmartCard, CodigoSapId, PalletId, 
				PalletIdIngreso, PalletIdAlmacen, PalletIdEmpaque, CajaEmpaqueId, CajaDespachoId, EstadoId, 
				TipoOrigenId, OrigenId, TipologiaId, NivelId, Tramite, Documento, EmpresaTransportadoraId, 
				Guia, Caja, Falla, Causa, Gaveta, Fuente, TecnicoCliente, PrealertaId, Cruce, Novedad, 
				GarantiaFabricante, GarantiaWoden, ObservacionId, Observaciones, UsuarioId, Fecha, Bodega, 
				EstadoCliente, EstadoEquipo, EstadoActual, UsuarioIdMovimiento, FechaMovimiento, transportadoraId, 
				ciudadId, loteId, smartCardId, cajaIngresoId, numeroSmartcard, FechaInventarioTermina, DocumentoInventarioTermina,UsuarioIdTermina
			)
			SELECT 
				i.Id, i.Serial, i.Mac, i.Serial3, i.Serial4, i.Serial5, i.SmartCard, i.CodigoSapId, i.PalletId, 
				i.PalletIdIngreso, i.PalletIdAlmacen, i.PalletIdEmpaque, i.CajaEmpaqueId, i.CajaDespachoId, i.EstadoId, 
				i.TipoOrigenId, i.OrigenId, i.TipologiaId, i.NivelId, i.Tramite, i.Documento, i.EmpresaTransportadoraId, 
				i.Guia, i.Caja, i.Falla, i.Causa, i.Gaveta, i.Fuente, i.TecnicoCliente, i.PrealertaId, i.Cruce, i.Novedad, 
				i.GarantiaFabricante, i.GarantiaWoden, i.ObservacionId, i.Observaciones, i.UsuarioId, i.Fecha, i.Bodega, 
				i.EstadoCliente, i.EstadoEquipo, i.EstadoActual, i.UsuarioIdMovimiento, i.FechaMovimiento, i.transportadoraId, 
				i.ciudadId, i.loteId, i.smartCardId, i.cajaIngresoId, i.numeroSmartcard, GETDATE(), @Documento,@UsuarioId
			FROM Ingreso i LEFT JOIN InventarioTerminado t ON i.Id = t.SerialId
			WHERE t.SerialId IS NULL
			select @Filas=@@ROWCOUNT
		END
go

CREATE PROCEDURE [dbo].[pa_InsertLabel]
@Nombre VARCHAR(30),
@Tipo VARCHAR(20),
@Impresion INT,
@CodigoSapId INT
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	BEGIN TRY
		BEGIN TRANSACTION
			INSERT INTO Etiqueta(nombre,tipo,impresion,codigoSapId)
			VALUES(@Nombre,@Tipo,@Impresion,@CodigoSapId)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InsertLabelField]
@EtiquetaId int,
@Nombre varchar(20),
@Valor varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	BEGIN TRY
		BEGIN TRANSACTION
			INSERT INTO EtiquetaCampo(EtiquetaId,Nombre,Valor)
			VALUES(@EtiquetaId,@Nombre,@Valor)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END



go


    CREATE PROCEDURE [dbo].[pa_InsertLabourUser]
        @ManodeObraId INT,
        @UsuarioId INT,
        @UsuarioActivo BIT,
        @Altas BIT,
        @Bajas BIT
        AS
        BEGIN
            INSERT INTO ManoDeObraUsuario(manoDeObraId,usuarioId,usuarioActivo,altas,bajas)VALUES(@ManoDeObraId,@UsuarioId,@UsuarioActivo,@Altas,@Bajas)
        END
go

CREATE PROCEDURE [dbo].[pa_InsertLote]
        @Nombre varchar(50),
        @Descripcion varchar(120)
        AS
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.
            BEGIN TRY
                BEGIN TRANSACTION	
                    INSERT INTO Lote(nombre,descripcion,activo)
                    VALUES(@Nombre,@Descripcion,1)
                COMMIT TRANSACTION
            END TRY
            BEGIN CATCH
                ROLLBACK TRANSACTION
            END CATCH
        END
go

CREATE PROCEDURE [dbo].[pa_InsertManoDeObra] @Fecha DATE, @Detalle VARCHAR(50) = NULL, @Observaciones VARCHAR(50) = NULL, @mano_obra_json NVARCHAR(MAX) = NULL, @usuario NVARCHAR(150) = NULL AS BEGIN BEGIN TRY BEGIN TRANSACTION INSERT INTO ManoDeObra (fecha, detalle, observaciones, mano_obra_json, activo, usuario, created_at, updated_at) VALUES (@Fecha, @Detalle, @Observaciones, @mano_obra_json, 1, @usuario, GETDATE(), GETDATE()); COMMIT TRANSACTION END TRY BEGIN CATCH ROLLBACK TRANSACTION END CATCH END;
go


CREATE PROCEDURE [dbo].[pa_InsertMaster]
@Codigo varchar(50),
@Descripcion varchar(50),
@Detalle varchar(150),
@Adicional varchar(50),
@TipoMaestroId int
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	BEGIN TRY
		BEGIN TRANSACTION	
			INSERT INTO Maestro(codigo,descripcion,Detalle,adicional,tipoMaestroId)
			VALUES(@Codigo,@Descripcion,@Detalle,@Adicional,@TipoMaestroId)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END
go


CREATE   PROCEDURE [dbo].[pa_InsertMasterPrinting]
@CajaEmpaqueId INT,
@UsuarioId INT
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	BEGIN TRY
		BEGIN TRANSACTION	
			INSERT INTO MasterImpresas(CajaEmpaqueId, UsuarioId)
			VALUES (@CajaEmpaqueId, @UsuarioId)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InsertMasterType]
@Nombre varchar(50),
@Descripcion varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	BEGIN TRY
		BEGIN TRANSACTION
			insert into TipoMaestro(nombre,descripcion)
			values(@Nombre,@Descripcion)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END




go

/**
 * Author:  Pedro Neira
 * Created: Mayo, 02  2024
 */ 
CREATE PROCEDURE [dbo].[pa_InsertPacking]
@SerialId INT,
@Serial VARCHAR(50),
@Mac VARCHAR(50),
@CodigoSapId INT,
@PalletId INT,
@CajaEmpaqueId INT,
@NivelId INT,
@UsuarioId INT,
@Fecha DATETIME,
@LoteId INT,
@SmartCardId INT,
@SmartCard VARCHAR(50),
@Filas int output
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON
	
	BEGIN TRY
		BEGIN TRANSACTION
			INSERT INTO Empaque(serialId,serial,mac,codigoSapId,palletId,cajaEmpaqueId,nivelId,usuarioId,fecha,loteId,smartCardId,smartCard)
			VALUES(@SerialId,UPPER(@Serial),UPPER(@Mac),@CodigoSapId,@PalletId,@CajaEmpaqueId,@NivelId,@UsuarioId,@Fecha,@LoteId,@SmartCardId,@SmartCard)
            select @Filas=@@ROWCOUNT
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END
go

CREATE PROCEDURE [dbo].[pa_InsertPackingWebD]
    @SerialId      INT,
    @Serial        VARCHAR(50),
    @Mac           VARCHAR(50),
    @CodigoSapId   INT,
    @PalletId      INT,
    @CajaEmpaqueId INT,
    @EstadoId	   INT,
    @NivelId       INT,
    @UsuarioId     INT,
    @Fecha         DATETIME,
    @LoteId        INT,
    @SmartCardId   INT,
    @SmartCard     VARCHAR(50),
    @Filas         INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
 
    BEGIN TRY
        BEGIN TRANSACTION;
 
        -- 1) Lógica de eliminación si existe el SerialId
        -- Esto asegura que no haya duplicados y que la inserción sea limpia
        DELETE FROM Empaque 
        WHERE serialId = @SerialId;

        -- 2) Inserción del nuevo registro
        INSERT INTO Empaque (
              serialId, serial, mac, codigoSapId,
              palletId, cajaEmpaqueId, nivelId,
              usuarioId, fecha, loteId,
              smartCardId, smartCard
        )
        VALUES (
              @SerialId, UPPER(@Serial), UPPER(@Mac), @CodigoSapId,
              @PalletId, @CajaEmpaqueId, @NivelId,
              @UsuarioId, @Fecha, @LoteId,
              @SmartCardId, @SmartCard
        );
 
        -- 3) Actualizar tabla Ingreso
        UPDATE i
           SET i.estadoId            = @EstadoId,
               i.palletId            = @PalletId,
               i.palletIdEmpaque     = @PalletId,
               i.cajaEmpaqueId       = @CajaEmpaqueId,
               i.CajaDespachoId      = 0,
               i.usuarioIdMovimiento = @UsuarioId,
               i.fechaMovimiento     = @Fecha,
               i.loteId              = @LoteId
          FROM Ingreso i
         WHERE i.serial = UPPER(@Serial);
 
        -- Asignamos el conteo de filas afectadas de la actualización de Ingreso
        SELECT @Filas = @@ROWCOUNT; 
 
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
 
        SET @Filas = 0;
        
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@ErrorMessage, 16, 1);
    END CATCH
END
go


CREATE PROCEDURE [dbo].[pa_InsertPackingWebD2]
    @SerialId         INT,
    @Serial           VARCHAR(50),
    @Mac              VARCHAR(50),
    @CodigoSapId      INT,
    @PalletId         INT,
    @CajaEmpaqueId    INT,
    @EstadoId         INT,
    @NivelId          INT,
    @UsuarioId        INT,
    @Fecha            DATETIME,
    @LoteId           INT,
    @SmartCardId      INT,
    @SmartCard        VARCHAR(50),
    -- Nuevos parámetros para el update de SmartCard (opcionales)
    @EstadoSmartCard  INT         = NULL,
    @SmartCardCode    VARCHAR(50) = NULL,
    @Filas            INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    -- ✅ Normalizar a mayúsculas UNA sola vez al inicio
    --    Así los WHERE usan la variable directamente → índices funcionan
    SET @Serial      = UPPER(@Serial);
    SET @Mac         = UPPER(ISNULL(@Mac, ''));
    SET @SmartCard   = UPPER(ISNULL(@SmartCard, ''));
    SET @SmartCardCode = UPPER(ISNULL(@SmartCardCode, ''));

    BEGIN TRY
        BEGIN TRANSACTION;

        -- 1) Eliminar duplicado si existe
        DELETE FROM Empaque WHERE serialId = @SerialId;

        -- 2) Insertar nuevo registro
        INSERT INTO Empaque (
            serialId, serial, mac, codigoSapId,
            palletId, cajaEmpaqueId, nivelId,
            usuarioId, fecha, loteId,
            smartCardId, smartCard
        )
        VALUES (
            @SerialId, @Serial, @Mac, @CodigoSapId,
            @PalletId, @CajaEmpaqueId, @NivelId,
            @UsuarioId, @Fecha, @LoteId,
            @SmartCardId, @SmartCard
        );

        -- 3) Actualizar Ingreso del SERIAL
        UPDATE i
           SET i.estadoId            = @EstadoId,
               i.palletId            = @PalletId,
               i.palletIdEmpaque     = @PalletId,
               i.cajaEmpaqueId       = @CajaEmpaqueId,
               i.CajaDespachoId      = 0,
               i.usuarioIdMovimiento = @UsuarioId,
               i.fechaMovimiento     = @Fecha,
               i.loteId              = @LoteId,
               -- Asignar smartCard al serial si aplica
               i.smartCardId         = CASE WHEN @SmartCardId <> 0 THEN @SmartCardId ELSE i.smartCardId END,
               i.smartCard           = CASE WHEN @SmartCard   <> '' THEN @SmartCard   ELSE i.smartCard   END
          FROM Ingreso i
         WHERE i.serial = @Serial;  -- ✅ Sin UPPER() → usa el índice

        SELECT @Filas = @@ROWCOUNT;

        -- 4) Actualizar Ingreso de la SMARTCARD (si viene informada)
        --    Reemplaza pa_UpdatePackingEntrySmartCard
        IF @SmartCardCode <> '' AND @SmartCardCode <> '0' AND @EstadoSmartCard IS NOT NULL
        BEGIN
            UPDATE i
               SET i.estadoId            = @EstadoSmartCard,
                   i.palletId            = @PalletId,
                   i.palletIdEmpaque     = @PalletId,
                   i.cajaEmpaqueId       = @CajaEmpaqueId,
                   i.usuarioIdMovimiento = @UsuarioId,
                   i.fechaMovimiento     = @Fecha,
                   i.loteId              = @LoteId,
                   i.smartCardId         = CASE WHEN @SmartCardId <> 0 THEN @SmartCardId ELSE i.smartCardId END,
                   i.smartCard           = CASE WHEN @SmartCard   <> '' THEN @SmartCard   ELSE i.smartCard   END
              FROM Ingreso i
             WHERE i.serial = @SmartCardCode;  -- ✅ Sin UPPER() → usa el índice
        END

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        SET @Filas = 0;
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@ErrorMessage, 16, 1);
    END CATCH
END
go


CREATE PROCEDURE [dbo].[pa_InsertPallet]
@Numero varchar(20),
@PosicionId int,
@CodigoSapId int,
@TipologiaId int,
@OrigenId int,
@DestinoId int,
@UsuarioId int,
@LoteId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
BEGIN TRAN;
DECLARE @FechaBase DATE = CAST(GETDATE() AS DATE); 
DECLARE @Fecha CHAR(6) = 
RIGHT(CONVERT(CHAR(4), YEAR(@FechaBase)), 2) +
RIGHT('0' + CAST(MONTH(@FechaBase) AS VARCHAR), 2) +
RIGHT('0' + CAST(DAY(@FechaBase) AS VARCHAR), 2);
DECLARE @Consecutivo INT;
 
-- Intentar actualizar primero
UPDATE 
WmsWdGeneral.dbo.ConsecutivoPallet WITH (UPDLOCK, HOLDLOCK)
SET @Consecutivo = UltimoNumero = UltimoNumero + 1
WHERE Fecha = @Fecha;
 
-- Si no existe, lo creamos
IF @@ROWCOUNT = 0
BEGIN
    INSERT INTO WmsWdGeneral.dbo.ConsecutivoPallet (Fecha, UltimoNumero)
    VALUES (@Fecha, 1);
 
    SET @Consecutivo = 1;
END
 
-- Construir número final
SET @Numero = CAST(@Fecha + RIGHT('000000' + CAST(@Consecutivo AS VARCHAR), 6) AS BIGINT);
 
COMMIT;
	
	BEGIN TRY
		BEGIN TRANSACTION
			insert into Pallet(numero,posicionId,codigoSapId,tipologiaId,origenId,destinoId,usuarioId,fecha,usuarioIdModifica,fechaModifica,activo,loteId)
			values (@Numero,@PosicionId,@CodigoSapId,@TipologiaId,@OrigenId,@DestinoId,@UsuarioId,GETDATE(),@UsuarioId,GETDATE(),1,@LoteId); 
			
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InsertPart]
@ParteId int,
@CodigoSapId int,
@EstadoId int,
@Fecha datetime,
@UsuarioId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	BEGIN TRY
		BEGIN TRANSACTION
			insert into parte(parteid,codigosapId,estadoId,fecha,usuarioId,usuarioIdMovimiento,fechaMovimiento)
			values(@ParteId,@CodigoSapId,@EstadoId,@Fecha,@UsuarioId,@UsuarioId,@Fecha)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END




go


CREATE PROCEDURE [dbo].[pa_InsertPrealert]
@Nombre varchar(50),
@TipoOrigenId int,
@OrigenId int,
@Guia varchar(30),
@UsuarioId int,
@Fecha datetime,
@IdResponsable int,
@Estado varchar(20)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    BEGIN TRY
		BEGIN TRANSACTION
			insert into Prealerta(nombre,tipoOrigenId,origenId,guia,usuarioId,fecha,IdResponsable,Estado)
			values(@Nombre,@TipoOrigenId,@OrigenId,@Guia,@UsuarioId,@Fecha,@IdResponsable,@Estado)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END

go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InsertPrealertSerial]
@PrealertaId int,
@Serial varchar(50),
@Mac varchar(50),
@CodigoSap varchar(20),
@Descripcion varchar(150),
@Cantidad int,
@Caja int,
@Falla varchar(100),
@TecnicoCliente varchar(50),
@Pedido varchar(30),
@Tramite varchar(30),
@Novedad varchar(30),
@Garantia int
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	BEGIN TRY
		BEGIN TRANSACTION
			insert into PrealertaSerial(prealertaId,serial,mac,codigoSap,descripcion,cantidad,caja,falla,tecnicoCliente,pedido,tramite,novedad,garantia)
			values(@PrealertaId,@Serial,@Mac,@CodigoSap,@Descripcion,@Cantidad,@Caja,@Falla,@TecnicoCliente,@Pedido,@Tramite,@Novedad,@Garantia)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END




go

CREATE PROCEDURE [dbo].[pa_InsertPrealertaSerial]
    @PrealertaId varchar(50),
    @Serial varchar(50),
    @Mac varchar(50),
    @CodigoSap varchar(50),
    @Descripcion varchar(50),
    @Cantidad int,
    @Caja int ,
    @Falla varchar(100),
    @TecnicoCliente varchar(50),
    @Pedido nvarchar(50),
    @Tramite nvarchar(50),
    @Novedad varchar(100),
    @Garantia bit,
    @En_recogida tinyint,
    @Recogida tinyint,
    @Cantidad_recogida int,
    @Tipo varchar(50),
    @LoteId INT,
    @Observaciones varchar(120)
    AS
    BEGIN
        -- SET NOCOUNT ON added to prevent extra result sets from
        -- interfering with SELECT statements.
 
        BEGIN TRY
            BEGIN TRANSACTION
                INSERT INTO [dbo].[PrealertaSerial]([PrealertaId],[Serial],[Mac],[CodigoSap],[Descripcion],[Cantidad],[Caja],[Falla],[TecnicoCliente],[Pedido],[Tramite],[Novedad],[Garantia],[en_recogida],[recogida],[cantidad_recogida],[tipo],[loteId],[observaciones])
                VALUES(@PrealertaId,UPPER(@Serial),UPPER(@Mac),(SELECT codigo FROM CodigoSap WHERE Codigo =@CodigoSap),(SELECT descripcion FROM CodigoSap WHERE Codigo =@CodigoSap),@Cantidad,@Caja,@Falla,@TecnicoCliente,@Pedido,@Tramite,@Novedad,@Garantia,@En_recogida,@Recogida,@Cantidad_recogida,@Tipo,@LoteId,@Observaciones)
            COMMIT TRANSACTION
        END TRY
        BEGIN CATCH
            ROLLBACK TRANSACTION
        END CATCH
    END
go

CREATE PROCEDURE [dbo].[pa_InsertPresupuesto]
        @Fecha DATE,
        @SegmentoId INT,
        @NivelId INT,
        @Unidades varchar(50),
        @Presupuesto VARCHAR(50),
        @Reclamo VARCHAR(50),
        @Dias varchar(50),
        @Tipo varchar(50),
        @Detalle VARCHAR(50),
        @Observaciones VARCHAR(50)
        AS
        BEGIN
            
            BEGIN TRY
                BEGIN TRANSACTION	
                    INSERT INTO Presupuesto(fecha,segmentoId,nivelId,unidades,presupuesto,reclamo,diasHabiles,tipo,detalle,observaciones)
                    VALUES(@Fecha,@SegmentoId,@NivelId,@Unidades,@Presupuesto,@Reclamo,@Dias,@Tipo,@Detalle,@Observaciones)
                COMMIT TRANSACTION
            END TRY
            BEGIN CATCH
                ROLLBACK TRANSACTION
            END CATCH
        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InsertQuality]
@SerialId INT,
@Serial VARCHAR(50),
@Mac VARCHAR(50),
@CodigoSapId INT,
@PalletId INT,
@CajaEmpaqueId INT,
@UsuarioId INT,
@Fecha DATETIME
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_InsertQuality @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	BEGIN TRY
		BEGIN TRANSACTION
			INSERT INTO Calidad(serialId,serial,mac,codigoSapId,palletId,CajaEmpaqueId,usuarioId,fecha)
			VALUES(@SerialId,@Serial,@Mac,@CodigoSapId,@PalletId,@CajaEmpaqueId,@UsuarioId,@Fecha)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH;

END




go


CREATE PROCEDURE [dbo].[pa_InsertRepair]
@SerialId int,
@Serial varchar(50),
@Mac varchar(50),
@CodigoSapId int,
@EstadoFinalId int,
@FallaDxId int,
@TecnicoAsignacionId int,
@FechaAsignacion datetime,
@UsuarioId INT
AS
BEGIN
	
	BEGIN TRY BEGIN TRANSACTION
	INSERT INTO Reparacion(serialId,serial,mac, CodigoSapId, estadoFinalId, FallaDxId, tecnicoAsignacionId, fechaAsignacion, UsuarioId)
	VALUES(@SerialId,
		   @Serial,
		   @Mac,
		   @CodigoSapId,
		   @EstadoFinalId,
		   @FallaDxId,
		   @TecnicoAsignacionId,
		   @FechaAsignacion,
		   @UsuarioId)
	COMMIT TRANSACTION END TRY BEGIN CATCH
	ROLLBACK TRANSACTION END CATCH

END
go

CREATE PROCEDURE [dbo].[pa_InsertSapCode]
@Codigo VARCHAR(30),
@Descripcion VARCHAR(100),
@FamiliaId INT,
@TipoId INT,
@Validacion INT,
@Direccion VARCHAR(20),
@Largos VARCHAR(50),
@Recorte INT,
@Reingreso INT,
@ClasificacionId INT,
@NumSerial INT,
@Asignacion INT,
@LargosMac VARCHAR(50),
@LargosSerial3 VARCHAR(50),
@LargosSerial4 VARCHAR(50),
@LargosSerial5 VARCHAR(50),
@tipoEquipoId INT,
@areaId INT,
@ModeloId INT,
@ProveedorId INT,
@ValidacionMac INT,
@DireccionMac VARCHAR(20),
@RecorteMac INT,
@AsignacionFalla INT,
@Multimodelo INT,
@CantidadCaja INT,
@Smartcard INT
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	BEGIN TRY
		BEGIN TRANSACTION
			INSERT INTO CodigoSap(Codigo,Descripcion,FamiliaId,TipoId,Validacion,Direccion,Largos,Recorte,Reingreso,ClasificacionId,NumSerial,AsignacionAcc,LargosMac,LargosSerial3,LargosSerial4,LargosSerial5,tipoEquipoId,areaId,modeloId,proveedorId,validacionMac,direccionMac,recorteMac,AsignacionFalla,multimodelo,cantidadCaja,smartcard)
			VALUES(@Codigo,@Descripcion,@FamiliaId,@TipoId,@Validacion,@Direccion,@Largos,@Recorte,@Reingreso,@ClasificacionId,@NumSerial,@Asignacion,@LargosMac,@LargosSerial3,@LargosSerial4,@LargosSerial5,@tipoEquipoId,@areaId,@ModeloId,@ProveedorId,@ValidacionMac,@DireccionMac,@RecorteMac,@AsignacionFalla,@Multimodelo,@CantidadCaja,@Smartcard)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH;

END
go


    CREATE PROCEDURE [dbo].[pa_InsertSapCodeFailureComponent]
        @CodigoSapId INT,
        @FallaId INT,
        @ComponenteId INT
        AS
        BEGIN
            INSERT INTO codigoSapFallaComponente(codigoSapFallaId,componenteId)VALUES((SELECT id  FROM codigoSapFalla where codigoSapId = @CodigoSapId AND fallaId = @FallaId ),@ComponenteId)
        END
go

CREATE PROCEDURE [dbo].[pa_InsertSmartCard]
            @SerialId INT,
            @Serial VARCHAR(50),
            @CodigoSapId INT,
            @UsuarioId INT
            AS

            BEGIN
                
                BEGIN TRY
                    BEGIN TRANSACTION
                        INSERT INTO SmartCard(serialId,serial,codigoSapId,EstadoFinalId,fallaId,usuarioId,fecha)
                        VALUES(@SerialId,@Serial,@CodigoSapId,0,0,@UsuarioId,GETDATE())
                    COMMIT TRANSACTION
                END TRY
                BEGIN CATCH
                    ROLLBACK TRANSACTION
                END CATCH;
            END
go

CREATE PROCEDURE [dbo].[pa_InsertTransportadora]
        @Nombre varchar(120),
        @Direccion varchar(120),
        @Telefono varchar(20)
        AS
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.
            BEGIN TRY
                BEGIN TRANSACTION	
                    INSERT INTO Transportadora(nombre,direccion,telefono,activo)
                    VALUES(@Nombre,@Direccion,@Telefono,1)
                COMMIT TRANSACTION
            END TRY
            BEGIN CATCH
                ROLLBACK TRANSACTION
            END CATCH
        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InsertUnreadable]
@ConsecutivoSerial VARCHAR(50),
@ConsecutivoMac VARCHAR(50),
@SerialId INT,
@Fecha DATETIME
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	BEGIN TRY
		BEGIN TRANSACTION
			INSERT INTO Ilegible(consecutivoSerial,consecutivoMac,serialId,confirmado,fecha)
			VALUES(@ConsecutivoSerial,@ConsecutivoMac,@SerialId,0,@Fecha)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END




go


CREATE PROCEDURE [dbo].[pa_InsertWorflow]
    @ModuloId int,
    @OrigenId int,
    @OpcionId int,
    @TipologiaId int,
    @NivelId int
AS
BEGIN
    BEGIN TRY
        BEGIN TRANSACTION;
            INSERT INTO WorkFlow (moduloId, origenId, opcionId, tipologiaId, nivelId)
            VALUES (@ModuloId, @OrigenId, @OpcionId, @TipologiaId, @NivelId);
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
    END CATCH;
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_InsertWorflowDestiny]
@WorkflowId int,
@DestinoId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	BEGIN TRY
		BEGIN TRANSACTION
			insert into WorkFlowDestino(workFlowId,destinoId)
			values(@WorkflowId,@DestinoId)
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
	END CATCH

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_Layout]
AS
BEGIN
	SET NOCOUNT ON;

	SELECT id,Numero,'LIBRES' AS Estado FROM Posicion WHERE Id NOT IN (SELECT p.PosicionId FROM Pallet p INNER JOIN Estado e on p.DestinoId=e.Id WHERE e.Nombre='ALMACENAMIENTO' AND p.Activo=1)UNION
	SELECT id,Numero,'OCUPADAS' AS Estado FROM Posicion WHERE Id IN (SELECT p.PosicionId FROM Pallet p INNER JOIN Estado e on p.DestinoId=e.Id WHERE e.Nombre='ALMACENAMIENTO' AND p.Activo=1) order by Numero
END


go

CREATE PROCEDURE [dbo].[pa_NivelBefore]
@Serial Varchar(50),
@Nivel INT OUT
AS
BEGIN	
	SET NOCOUNT ON;
	DECLARE @CodigoId INT

	SELECT @CodigoId = ISNULL((SELECT MAX(NivelId) FROM Despacho WHERE Serial=@Serial AND Fecha BETWEEN (GETDATE()-184) AND GETDATE()),'0')

	SELECT @CodigoId

	IF @CodigoId != '0'
	BEGIN
	SELECT @Nivel= Codigo FROM Maestro WHERE Id
		=(SELECT MAX(NivelId) FROM Despacho WHERE Serial=@Serial AND Fecha BETWEEN (GETDATE()-184) AND GETDATE())
	END
	ELSE 
	BEGIN
	 SELECT @Nivel='0'
	END
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_PackOffBoxDispatch]
@PalletId INT,
@CajaDespachoId INT,
@EstadoId INT,
@Documento VARCHAR(30),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	UPDATE Despacho SET estadoId=@EstadoId,pedidoSap=@Documento,fecha=GETDATE()
	WHERE palletId=@PalletId and CajaDespachoId=@CajaDespachoId
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_PackOffBoxEntry]
@PalletId int,
@CajaDespachoId int,
@UsuarioId int
AS

BEGIN

	DELETE FROM Ingreso 
	WHERE palletId=@PalletId and cajaDespachoId=@CajaDespachoId

END




go

CREATE PROCEDURE [dbo].[pa_PackOffPalletAccesory]
        @PalletId int
        AS
        BEGIN
            DELETE FROM Accesorio
            WHERE palletId=@PalletId

        END
go

CREATE PROCEDURE [dbo].[pa_PackOffPalletAccesoryBox]
        @PalletId int,
        @CajaDespachoId int
        AS
        BEGIN
            DELETE a FROM Accesorio a
            INNER JOIN  Ingreso i ON a.SerialEmpaque = i.Serial
            WHERE a.PalletId=@PalletId  AND i.CajaDespachoId = @CajaDespachoId

        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_PackOffPalletDispatch]
@PalletId INT,
@EstadoId INT,
@Documento VARCHAR(30),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	UPDATE Despacho SET estadoId=@EstadoId,pedidoSap=@Documento,fecha=GETDATE()
	WHERE palletId=@PalletId
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_PackOffPalletEntry]
@PalletId int,
@UsuarioId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	DELETE FROM Ingreso 
	WHERE palletId=@PalletId

END




go

CREATE PROCEDURE [dbo].[pa_PackingBoxId]
        @PalletId int,
        @Numero int
        AS
        BEGIN
            SELECT Id
            FROM CajaEmpaque WITH(NOLOCK) 
            WHERE PalletId=@PalletId and Numero=@Numero
        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_QueryReport]
@Consulta varchar(max)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	exec (@Consulta)

END





go

CREATE PROCEDURE [dbo].[pa_Reingresos]
@Serial Varchar(50),
@Cantidad INT OUT
AS
BEGIN
	
	SET NOCOUNT ON;
	SELECT @Cantidad= Count(d.Serial) FROM Despacho d
	WHERE d.Serial = @Serial
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_RemoveCiclicPallet]
@CiclicoId INT,
@PalletId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_RemoveCiclicPallet @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	DELETE FROM CiclicoPallet 
	WHERE CiclicoId=@CiclicoId AND PalletId=@PalletId

END




go


CREATE   PROCEDURE [dbo].[pa_SaveClasificacion]
    @Datos NVARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRAN;

    SELECT serial, estadoId, nivelId, usuarioId, estado
    INTO #Lote
    FROM OPENJSON(@Datos)
    WITH (
        serial    VARCHAR(50) '$.serial',
        estadoId  INT         '$.estadoId',
        nivelId   INT         '$.nivelId',
        usuarioId INT         '$.usuarioId',
        estado    VARCHAR(30) '$.estado'
    );

    -- 1) Lo que hacía pa_UpdateClasificationEntry (Ingreso)
    UPDATE i
    SET i.estadoId = l.estadoId,
        i.nivelId = l.nivelId,
        i.usuarioIdMovimiento = l.usuarioId,
        i.fechaMovimiento = SYSDATETIME()
    FROM Ingreso i WITH (ROWLOCK)
    JOIN #Lote l ON l.serial = i.Serial;

    -- 2) Lo que hacía pa_UpdateClasificacion (último registro por serial)
    UPDATE c
    SET c.EstadoEnviado = l.estado,
        c.nivelId = l.nivelId,
        c.Fecha = GETDATE()
    FROM Clasificacion c
    JOIN (
        SELECT serial, MAX(Id) AS MaxId
        FROM Clasificacion
        GROUP BY serial
    ) ult ON ult.MaxId = c.Id
    JOIN #Lote l ON l.serial = c.serial;

    COMMIT;
END
go


CREATE   PROCEDURE [dbo].[pa_SaveDiagnostic]
  @Datos NVARCHAR(MAX)
AS
BEGIN
  SET NOCOUNT ON;

  BEGIN TRY
    BEGIN TRANSACTION;

    UPDATE i
    SET  i.estadoId            = d.estadoId,
         i.usuarioIdMovimiento = d.usuarioId,
         i.fechaMovimiento     = GETDATE()
    FROM Ingreso i
    INNER JOIN OPENJSON(@Datos) WITH (
      serial    VARCHAR(50) '$.serial',
      estadoId  INT         '$.estadoId',
      usuarioId INT         '$.usuarioId'
    ) AS d ON i.serial = d.serial;

    UPDATE dia
    SET  dia.estadoFinalId    = d.estadoFinalId,
         dia.fallaId          = d.fallaId,
         dia.Fecha            = GETDATE(),
         dia.EstadoCalidadId  = d.estadoCalidadId
    FROM Diagnostico dia
    INNER JOIN OPENJSON(@Datos) WITH (
      serial          VARCHAR(50) '$.serial',
      estadoFinalId   INT         '$.estadoFinalId',
      fallaId         INT         '$.fallaId',
      estadoCalidadId INT         '$.estadoCalidadId'
    ) AS d ON dia.id = (
      SELECT MAX(id) FROM Diagnostico WHERE serial = d.serial
    );

    COMMIT TRANSACTION;

  END TRY
  BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
  END CATCH
END
go


CREATE   PROCEDURE [dbo].[pa_SearchAccesory]
@PalletId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT cs.Codigo,cs.Descripcion,a.TipoAccesorio,o.Codigo as origen,a.Documento,a.Guia,a.Caja from Accesorio a 
	inner join CodigoSap cs on a.CodigoSapId=cs.Id 
	inner join Maestro o on a.OrigenId=o.Id 
	where a.PalletId=@PalletId
END
go



CREATE   PROCEDURE [dbo].[pa_SearchAccesoryPallet]
@Numero varchar(20),
@Estado varchar(50),
@UsuarioId int
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Numero='')
	BEGIN
		select p.id,p.numero,cs.codigo,cs.descripcion,count(a.id)cantidad,t.codigo as tipologia,lo.nombre as lote from Pallet p 
		left join Accesorio a on p.id=a.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
        inner join CodigoSap cs  ON p.codigoSapId =  cs.id
        left join Lote lo ON p.loteId = lo.id
		where p.usuarioId=@UsuarioId and e.Nombre=@Estado and p.activo=1 
		group by p.id,p.numero,cs.codigo,cs.descripcion,t.codigo,lo.nombre
	END
	ELSE
	BEGIN
		select p.id,p.numero,cs.codigo,cs.descripcion,count(a.Id)cantidad,t.codigo as tipologia,lo.nombre as lote from Pallet p 
		left join Accesorio a on p.id=a.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
        inner join CodigoSap cs  ON p.codigoSapId =  cs.id
        left join Lote lo ON p.loteId = lo.id
		where p.Numero=@Numero and p.usuarioId=@UsuarioId and e.Nombre=@Estado and p.activo=1 
		group by p.id,p.numero,cs.codigo,cs.descripcion,t.codigo,lo.nombre
	END

END
go


    CREATE PROCEDURE [dbo].[pa_SearchAllComponents]
        @Falla VARCHAR(20)
        AS
        BEGIN
            
            set nocount on;
            SELECT c.Id,c.Codigo,c.detalle as nivel FROM Maestro c
            INNER JOIN TipoMaestro tm ON c.TipoMaestroId = tm.Id
            WHERE tm.Nombre ='Componentes' AND c.Activo = 1   AND c.Descripcion = @Falla     
        END
go

/**
 * Author:  Julian Agamez
 * Created: OCT, 18  2024
 */ 
CREATE PROCEDURE [dbo].[pa_SearchAllPackedAccesory]
@Estado varchar(20)
AS
BEGIN

    SET NOCOUNT ON;

	SELECT 
		cs.codigo, 
		cs.descripcion, 
		m.Codigo AS familia, 
		SUM(CASE WHEN a.TipoAccesorio = 'Nuevo' THEN 1 ELSE 0 END) AS Nuevos, 
		SUM(CASE WHEN a.TipoAccesorio = 'Reacondicionado' THEN 1 ELSE 0 END) AS Reacondicionados
	INTO #TempAccesorios
	FROM Accesorio a 
	INNER JOIN CodigoSap cs ON a.codigoSapId = cs.id 
	INNER JOIN dbo.estado e ON a.estadoId = e.id 
	INNER JOIN Maestro m ON cs.FamiliaId = m.Id
	WHERE 
		a.SerialEmpaque IN (SELECT SerialEmpaque FROM TempSeriales)
		AND e.nombre = @Estado
	GROUP BY 
		cs.codigo, 
		cs.descripcion, 
		m.Codigo;

	SELECT 
		codigo, 
		descripcion, 
		familia, 
		ISNULL(Nuevos, 0) AS Nuevos, 
		ISNULL(Reacondicionados, 0) AS Reacondicionados
	FROM #TempAccesorios;

	DROP TABLE #TempAccesorios;
	DELETE FROM TempSeriales;
    
END;
go

CREATE PROCEDURE [dbo].[pa_SearchAllPackingAccesory]
        @Estado varchar(20)
        AS
        BEGIN

        SET NOCOUNT ON;
            SELECT cs.codigo, cs.descripcion, ISNULL(count(a.id), 0) as cantidad 
            INTO #TempAcceNuevo
            FROM Accesorio a 
            INNER JOIN CodigoSap cs ON a.codigoSapId = cs.id 
            INNER JOIN dbo.estado e ON a.estadoId = e.id 
            WHERE e.nombre = @Estado AND cs.Codigo NOT IN (SELECT cs.codigo FROM Accesorio a INNER JOIN CodigoSap cs ON a.CodigoSapId = cs.Id WHERE a.SerialEmpaque IN (SELECT SerialEmpaque FROM TempSeriales)) AND a.TipoAccesorio = 'Nuevo'
            GROUP BY cs.codigo, cs.descripcion;

            SELECT cs.codigo, cs.descripcion, ISNULL(count(a.id), 0) as cantidad
            INTO #TempAcceReacon
            FROM Accesorio a 
            INNER JOIN CodigoSap cs ON a.codigoSapId = cs.id 
            INNER JOIN dbo.estado e ON a.estadoId = e.id 
            WHERE e.nombre = @Estado AND cs.Codigo NOT IN (SELECT cs.codigo FROM Accesorio a INNER JOIN CodigoSap cs ON a.CodigoSapId = cs.Id WHERE a.SerialEmpaque IN (SELECT SerialEmpaque FROM TempSeriales)) AND a.TipoAccesorio = 'Reacondicionado'
            GROUP BY cs.codigo, cs.descripcion;

            SELECT ISNULL(a.Codigo, n.codigo) as codigo, ISNULL(a.Descripcion, n.descripcion) as descripcion, ISNULL(n.cantidad, 0) as Nuevos, ISNULL(a.cantidad, 0) as Reacondicionados 
            FROM #TempAcceNuevo n
            FULL OUTER JOIN #TempAcceReacon a ON n.codigo = a.Codigo;

            DROP TABLE #TempAcceNuevo;
            DROP TABLE #TempAcceReacon;
            DELETE FROM TempSeriales;

        END;
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
create PROCEDURE [dbo].[pa_SearchAllSapCode]
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_SearchSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	SELECT cs.codigo,cs.descripcion,f.codigo AS familia FROM CodigoSap cs 
	INNER JOIN Maestro f ON cs.familiaId=f.id 
	ORDER BY cs.codigo

END



go


CREATE   PROCEDURE [dbo].[pa_SearchAssignedRepair]
@Perfil varchar(20),
@TecnicoAsignacionId int,
@Tipo varchar(20)
AS
BEGIN
	SET NOCOUNT ON;
	IF(@Tipo='SERIALES')
	BEGIN
		select r.serial,convert(date,r.fechaAsignacion)fechaAsignacion from Reparacion r
		OUTER APPLY ( SELECT MAX(r2.fechaAsignacion) AS maxFecha FROM Reparacion r2
        WHERE r2.serial = r.serial AND r2.tecnicoAsignacionId = @TecnicoAsignacionId) t
		inner join Ingreso i on r.serialId=i.id
		inner join WmsWdGeneral.dbo.Estado e on i.EstadoId=e.id
		inner join Maestro m on r.estadoFinalId=m.id
		where r.serial not like 'ILE%' and e.Nombre='REPARACION-ASIGNADO' and m.Codigo='ASIGNADO'
		and (@Perfil = 'ADMINISTRADOR' OR r.tecnicoAsignacionId=@TecnicoAsignacionId)
		and r.fechaAsignacion = t.maxFecha;
	END
	ELSE
	BEGIN
		select r.serial,convert(date,r.fechaAsignacion)fechaAsignacion from Reparacion r
		OUTER APPLY ( SELECT MAX(r2.fechaAsignacion) AS maxFecha FROM Reparacion r2
        WHERE r2.serial = r.serial AND r2.tecnicoAsignacionId = @TecnicoAsignacionId) t
		inner join Ingreso i on r.serialId=i.id
		inner join WmsWdGeneral.dbo.Estado e on i.EstadoId=e.id
		inner join Maestro m on r.estadoFinalId=m.id
		where r.serial like 'ILE%' and e.Nombre='REPARACION-ASIGNADO' and m.Codigo='ASIGNADO'
		and (@Perfil = 'ADMINISTRADOR' OR r.tecnicoAsignacionId=@TecnicoAsignacionId)
		and r.fechaAsignacion = t.maxFecha;
	END
END
go


CREATE PROCEDURE [dbo].[pa_SearchBase]
@Base VARCHAR(50)
AS
/*
DECLARE @Base VARCHAR(50)
SET @Base=''
EXEC pa_SearchBase @Base
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Base='BaseDespacho')
	BEGIN
		SELECT * FROM BaseDespacho
	END
	ELSE
	IF(@Base='BaseEmpaque')
	BEGIN
		SELECT * FROM BaseEmpaque
	END
	ELSE
	IF(@Base='BaseInventario')
	BEGIN
		SELECT * FROM BaseInventario
	END
	ELSE
	IF(@Base='BaseSeparacion')
	BEGIN
		SELECT * FROM BaseSeparacion
	END
	ELSE
	IF(@Base='BaseIngreso')
	BEGIN
		SELECT * FROM BaseIngreso
	END
    IF(@Base='BaseEtiquetado')
	BEGIN
		SELECT * FROM BaseEtiquetado
	END

END
go


CREATE PROCEDURE [dbo].[pa_SearchBatch]
    @Lote varchar(50)
AS
BEGIN
    SET NOCOUNT ON;

    IF(@Lote = '')
    BEGIN
        SELECT id,
               nombre,
               descripcion,
               activo
        FROM Lote
        ORDER BY nombre;
    END
    ELSE
    BEGIN
        SELECT id,
               nombre,
               descripcion,
               activo
        FROM Lote
        WHERE nombre = @Lote
        ORDER BY nombre;
    END
END;
go

CREATE PROCEDURE [dbo].[pa_SearchBoxEntry]
@CajaEmpaqueId int
AS
/*

*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT i.serial,i.mac,cs.codigo,cs.descripcion,t.codigo AS tipologia,i.SmartCard FROM Ingreso i 
	INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id 
	INNER JOIN Maestro t ON i.tipologiaId=t.id 
	WHERE i.cajaEmpaqueId=@CajaEmpaqueId

END
go


CREATE PROCEDURE [dbo].[pa_SearchBoxEntryReingreso]
        @PalletId int,
        @CajaIngresoId int
        AS

        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.
            SET NOCOUNT ON;

            SELECT i.serial,i.mac,i.Serial3,i.Serial4,i.Serial5,cs.codigo AS codigosap,cs.descripcion,m.Codigo AS tipologia,mto.Codigo AS tipoOrigen,i.fecha,i.estadoCliente,l.Nombre AS lote,md.Codigo AS modelo, (SELECT COUNT(Serial) FROM Despacho WHERE Serial=i.Serial ) AS Reingreso FROM Ingreso i 
            INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id 
            INNER JOIN Maestro m ON i.TipologiaId = m.id
            INNER JOIN Maestro mto ON i.TipoOrigenId = mto.id
            LEFT JOIN Lote l ON i.loteId = l.id
            LEFT JOIN Maestro md ON i.modeloId = md.Id
            WHERE i.palletId=@PalletId AND i.CajaIngresoId = @CajaIngresoId
            GROUP BY i.serial,i.mac,i.Serial3,i.Serial4,i.Serial5,cs.codigo,cs.descripcion,m.codigo,mto.Codigo,i.fecha,i.estadoCliente,l.Nombre,md.Codigo
            ORDER BY i.fecha
        END
go


    CREATE PROCEDURE [dbo].[pa_SearchCarrier]
        @Transportadora varchar(50)
        AS

        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.
            
            set nocount on;

            IF(@Transportadora='')
            BEGIN
                SELECT id,nombre,direccion,telefono,activo
                FROM Transportadora
                ORDER BY nombre
            
            END
            ELSE
            BEGIN
                SELECT id,nombre,direccion,telefono,activo
                FROM Transportadora
                where nombre=@Transportadora 
                order by nombre
            END
        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchCiclic]
@Nombre VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_SearchCiclic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;
	
	IF(@Nombre='')
	BEGIN
		SELECT c.id,c.nombre,u.nombreusuario,c.estado FROM Ciclico c 
		INNER JOIN WmsWdGeneral.dbo.usuario u ON c.usuarioId=u.id 
		ORDER BY nombre
	END
	ELSE
	BEGIN
		SELECT c.id,c.nombre,u.nombreusuario,c.estado FROM Ciclico c 
		INNER JOIN WmsWdGeneral.dbo.usuario u ON c.usuarioId=u.id 
		WHERE Nombre=@Nombre
	END

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchCiclicPallet]
@CiclicoId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_SearchCiclicPallet @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;
	
	SELECT p.id,p.numero,count(i.serial)cantidad,t.codigo AS tipologia,po.numero AS posicion FROM Pallet p 
	INNER JOIN Ingreso i ON p.id=i.palletId 
	LEFT JOIN Posicion po ON p.posicionId=po.id 
	INNER JOIN Maestro t ON p.tipologiaId=t.id 
	WHERE p.id IN 
	(SELECT palletId FROM CiclicoPallet 
	WHERE ciclicoId=@CiclicoId)
	GROUP BY p.Id,p.Numero,t.Codigo,po.Numero

END




go


    CREATE PROCEDURE [dbo].[pa_SearchCity]
        @Ciudad varchar(50)
        AS

        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.
            
            set nocount on;

            IF(@Ciudad='')
            BEGIN
                SELECT id,nombre,codigoCiudad,activo
                FROM Ciudad
                ORDER BY nombre
            
            END
            ELSE
            BEGIN
                SELECT id,nombre,codigoCiudad,activo
                FROM Ciudad
                where nombre=@Ciudad 
                order by nombre
            END
        END
go

CREATE   PROCEDURE [dbo].[pa_SearchClasificationEntry]                                                                                                                                                 
AS                                                                                                                                                                
BEGIN                                                                                                                                                             
 -- SET NOCOUNT ON added to prevent extra result sets from                                                                                                        
 -- interfering with SELECT statements.                                                                                                                           
 SET NOCOUNT ON;                                                                                                                                                  
 SELECT DISTINCT i.serial,i.mac,cs.codigo,cs.descripcion FROM Ingreso i                                                                                             
 INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id                                                                                                              
 INNER JOIN WmsWdGeneral.dbo.estado es ON i.estadoId=es.id                                                                                                       
 WHERE ((es.nombre='CLASIFICACION-REPARACION'))                                                       
END
go


        CREATE   PROCEDURE [dbo].[pa_SearchCleaningAccesory]
        @TipoAccesorio varchar(20),
        @Codigo varchar(50)
        AS
        BEGIN
            SET NOCOUNT ON;	
            SELECT cs.Codigo,
                   cs.Descripcion,
                   m.Codigo AS EstadoLimpieza,
                   u.NombreUsuario AS UsuarioLimpieza,
                   a.CodigoSapId,
                   a.EstadoLimpiezaId,
                   a.UsuarioLimpiezaId
            FROM Accesorio a
            INNER JOIN CodigoSap cs ON a.codigoSapId=cs.id
            INNER JOIN WmsWdGeneral.dbo.estado e ON a.estadoId=e.id
            INNER JOIN Maestro m ON a.EstadoLimpiezaId=m.id
            LEFT JOIN Usuario u ON a.UsuarioLimpiezaId = u.id
            WHERE e.nombre = 'LIMPIEZA-ASIGNADO'
        END
go


CREATE   PROCEDURE [dbo].[pa_SearchCleaningEntry]
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        CONCAT(i.Serial, ', ', i.Serial3) AS serial, 
        i.mac,
        cs.Codigo AS codigosap,
        cs.Descripcion AS descripcion, 
        u.NombreUsuario AS usuarioasignado
    FROM 
        Ingreso i
        INNER JOIN CodigoSap cs ON i.CodigoSapId = cs.Id    
        INNER JOIN WmsWdGeneral.dbo.Estado e ON i.EstadoId = e.Id
        INNER JOIN Limpieza l ON i.Id = l.SerialId
        INNER JOIN WmsWdGeneral.dbo.Usuario u ON u.Id = l.UsuarioAsignadoId
    WHERE 
        e.Nombre = 'LIMPIEZA-TERMINADO'
        AND l.Id = (SELECT MAX(l2.Id) FROM Limpieza l2 WHERE l2.SerialId = i.Id);
END;
go


CREATE PROCEDURE [dbo].[pa_SearchCodeSap_Acc]
@CodigoSapId int
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	SELECT id,codigo,descripcion FROM CodigoSap 
	WHERE id IN(SELECT AccesorioId FROM CodigoSapAccesorio 
	WHERE CodigoSapId=@CodigoSapId)

END
go

/**
 * Author:  Pedro Neira
 * Created: FEB, 27 2024
 */

CREATE PROCEDURE [dbo].[pa_SearchComponents]
    @Serial VARCHAR(50),
    @Falla VARCHAR(50)
    AS
    
    BEGIN

        SET NOCOUNT ON;
        SELECT c.id, c.codigo, c.detalle as nivel
        FROM Maestro c
        INNER JOIN TipoMaestro tm ON c.TipoMaestroId = tm.Id
        WHERE  tm.Nombre = 'Componentes' 
        AND c.id NOT IN (SELECT m.id FROM Maestro m
                        INNER JOIN SerialComponente sc ON m.id = sc.ComponenteId
                        WHERE sc.SerialId = (SELECT id FROM ingreso WHERE Serial = @Serial) 
                        )
        AND c.Descripcion = @Falla
        AND c.Activo =1
        ORDER BY c.codigo
    END
go

CREATE PROCEDURE [dbo].[pa_SearchComponentsAsig]
    @Serial VARCHAR(50),
    @Falla VARCHAR(50),
    @Filas  INT OUTPUT
    AS
    BEGIN

        SET NOCOUNT ON;
            SELECT  m.id,m.codigo,m.detalle AS nivel FROM Maestro m
            INNER JOIN SerialComponente sc ON m.id = sc.ComponenteId
            WHERE sc.SerialId = (SELECT id FROM ingreso WHERE Serial = @Serial)
            AND m.Descripcion = @Falla
            ORDER BY m.codigo
            SELECT @Filas = @@ROWCOUNT
    END
go

CREATE PROCEDURE [dbo].[pa_SearchComponentsAsigSerial]
			@SerialId INT,			
			@Filas  INT OUTPUT
			AS
			BEGIN

				SET NOCOUNT ON;
					SELECT serial FROM SerialComponente WHERE SerialId = @SerialId
					SELECT @Filas = @@ROWCOUNT
			END
go


CREATE   PROCEDURE [dbo].[pa_SearchCosmeticaEntry]
@Perfil varchar(20),
@UsuarioId Int
AS
BEGIN
 SET NOCOUNT ON;
 SELECT DISTINCT i.serial,i.mac,cs.codigo,cs.descripcion FROM Ingreso i
 INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id
 INNER JOIN WmsWdGeneral.dbo.estado es ON i.estadoId=es.id
 WHERE ((es.nombre='COSMETICA')) AND (@Perfil = 'ADMINISTRADOR' OR i.UsuarioIdMovimiento = @UsuarioId)
END;
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchCyclicPallet]
@Numero varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Numero='')
	BEGIN
		select p.id,p.numero,count(i.serial)cantidad,t.codigo as tipologia,po.numero as posicion from Pallet p 
		left join Ingreso i on p.id=i.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		left join Posicion po on p.posicionId=po.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
		where e.Nombre='ALMACENAMIENTO' and p.activo=1 
		group by p.id,p.numero,t.codigo,po.numero
	END
	ELSE
	BEGIN
		select p.id,p.numero,count(i.serial)cantidad,t.codigo as tipologia,po.numero as posicion from Pallet p 
		left join Ingreso i on p.id=i.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		left join Posicion po on p.posicionId=po.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
		where p.Numero=@Numero and e.Nombre='ALMACENAMIENTO' and p.activo=1 
		group by p.id,p.numero,t.codigo,po.numero
	END

END




go


-- =============================================
-- Author:		MEDINA DAVID
-- ALTER date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchDeliveryAccesory]
@PalletId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	select a.id,cs.codigo,cs.descripcion from Accesorio a 
	inner join CodigoSap cs on a.codigoSapId=cs.id 
	where a.PalletId=@PalletId

END







go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchDeliveryEntry]
@Pallet varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT i.serial FROM Ingreso i WITH(NOLOCK) 
	INNER JOIN Pallet p ON i.palletId=p.id 
	WHERE p.numero=@Pallet

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchDeliveryPart]
@PalletId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	select p.id,m.codigo as parte,cs.Codigo from parte p 
	inner join CodigoSap cs on p.CodigoSapId=cs.id 
	inner join maestro m on p.parteid=m.id 
	where p.palletid=@PalletId

END




go

CREATE   PROCEDURE [dbo].[pa_SearchDiagnosticEntry]
@EstadoFinal varchar(20),
@Perfil VARCHAR(50),
@UsuarioId INT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	SELECT DISTINCT i.serial,i.mac,cs.codigo,cs.descripcion,f.descripcion AS falla,e.descripcion AS estado FROM Ingreso i 
	CROSS APPLY (SELECT TOP 1 r2.Id, r2.EstadoFinalId FROM Diagnostico r2 WHERE r2.SerialId = i.Id ORDER BY r2.Id DESC) ult
	INNER JOIN Diagnostico d ON i.Id=d.serialId
	INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id
	INNER JOIN Maestro f ON d.fallaId=f.id 
	INNER JOIN Maestro e ON d.estadoFinalId=e.id 
	INNER JOIN WmsWdGeneral.dbo.estado es ON i.estadoId=es.id 
	WHERE ((es.nombre='DIAGNOSTICO-TERMINADO') and (e.codigo=@EstadoFinal))
	AND (@Perfil = 'ADMINISTRADOR' OR (i.UsuarioIdMovimiento = @UsuarioId AND @Perfil <> 'ADMINISTRADOR'))
END
go


CREATE PROCEDURE [dbo].[pa_SearchDispatch]
@PalletId INT
AS

BEGIN

	SET NOCOUNT ON;

    SELECT cd.id,cd.numero,count(ing.id)seriales FROM CajaDespacho cd
    LEFT JOIN (select i.Id,i.CajaDespachoId FROM INGRESO i 
                INNER JOIN CodigoSap cs ON i.CodigoSapId = cs.Id
                INNER JOIn Maestro m ON cs.TipoId = m.Id
                WHERE m.Codigo = 'SERIALIZADOS')ing ON cd.id=ing.cajaDespachoId 
    WHERE cd.palletId=@PalletId and cd.activo=1  
	GROUP BY cd.id,cd.numero 
	ORDER BY cd.numero

END
go


CREATE PROCEDURE [dbo].[pa_SearchEntry]
@PalletId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT CONCAT(i.serial,', ',i.Serial3,', ',i.Serial4,', ',i.Serial5) AS serial,i.mac,cs.codigo AS codigosap,cs.descripcion,i.guia,i.documento,i.fecha, (SELECT COUNT(Serial) FROM Despacho WHERE Serial=i.Serial ) AS Reingreso FROM Ingreso i 
	INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id 
	WHERE i.palletId=@PalletId
	GROUP BY i.serial,i.serial3,i.serial4,i.serial5,i.mac,cs.codigo,cs.descripcion,i.guia,i.documento,i.fecha

END
go


CREATE PROCEDURE [dbo].[pa_SearchEntryPallet]
@Numero varchar(20),
@Estado varchar(50),
@UsuarioId int
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Numero='')
	BEGIN
		select p.id,p.numero,cs.Codigo,cs.descripcion,count(i.serial)cantidad,t.codigo as tipologia,lo.Nombre as lote from Pallet p 
		left join Ingreso i on p.id=i.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
        inner join CodigoSap cs  ON p.codigoSapId =  cs.id
        left join Lote lo ON p.loteId = lo.id
		where p.usuarioId=@UsuarioId and e.Nombre=@Estado and p.activo=1 
		group by p.id,p.numero,cs.codigo,t.codigo,lo.Nombre,cs.descripcion
	END
	ELSE
	BEGIN
		select p.id,p.numero,cs.codigo,cs.descripcion,count(i.serial)cantidad,t.codigo as tipologia,lo.Nombre as Lote from Pallet p 
		left join Ingreso i on p.id=i.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
        inner join CodigoSap cs  ON p.codigoSapId =  cs.id
        left join Lote lo ON p.loteId = lo.id
		where p.Numero=@Numero and p.usuarioId=@UsuarioId and e.Nombre=@Estado and p.activo=1 
		group by p.id,p.numero,cs.codigo,t.codigo,lo.nombre,cs.descripcion
	END

END;
go


CREATE PROCEDURE [dbo].[pa_SearchEntryReingreso]
@PalletId int
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT i.serial,i.mac,i.Serial3,i.Serial4,i.Serial5,cs.codigo AS codigosap,cs.descripcion,m.Codigo AS tipologia,mto.Codigo AS tipoOrigen,i.fecha,i.estadoCliente,l.Nombre AS lote,md.Codigo AS modelo, (SELECT COUNT(Serial) FROM Despacho WHERE Serial=i.Serial ) AS Reingreso FROM Ingreso i 
	INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id 
    INNER JOIN Maestro m ON i.TipologiaId = m.id
    INNER JOIN Maestro mto ON i.TipoOrigenId = mto.id
    LEFT JOIN Lote l ON i.loteId = l.id
    LEFT JOIN Maestro md ON i.modeloId = md.Id
	WHERE i.palletId=@PalletId
	GROUP BY i.serial,i.mac,i.Serial3,i.Serial4,i.Serial5,cs.codigo,cs.descripcion,m.codigo,mto.Codigo,i.fecha,i.estadoCliente,l.Nombre,md.Codigo
	ORDER BY i.fecha
END
go

CREATE PROCEDURE [dbo].[pa_SearchForecast] @Forecast NVARCHAR(80) = '' AS BEGIN SET NOCOUNT ON; SELECT f.id, f.linea_negocio, f.fecha, f.fecha_entrega, f.forecast_und, f.jornada, f.dias_habiles, f.observaciones, f.activo, f.usuario, f.created_at, f.updated_at, f.familias_json FROM Forecast f ORDER BY f.fecha DESC; END;
go

CREATE PROCEDURE [dbo].[pa_SearchGeneralInventoryPallet]
@Numero varchar(20),
@Estado varchar(20),
@UsuarioId INT

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;
	IF(@Estado = 'ALMACENAMIENTO')
		BEGIN
			IF(@Numero='')
			BEGIN
				select p.id,p.numero,count(i.serial)cantidad,po.numero as posicion from Pallet p 
				left join Ingreso i on p.id=i.palletId 
				left join Posicion po on p.posicionId=po.id 
				inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
				where e.nombre=@Estado and estadoInventario=1 and p.Activo =1
				group by p.id,p.numero,po.numero
			END
			ELSE
			BEGIN
				select p.id,p.numero,count(i.serial)cantidad,po.numero as posicion from Pallet p 
				left join Ingreso i on p.id=i.palletId 
				left join Posicion po on p.posicionId=po.id 
				inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
				where p.Numero=@Numero and e.nombre=@Estado and estadoInventario=1 and p.Activo =1
				group by p.id,p.numero,po.numero
			END
		END
	ELSE	
		BEGIN
			IF(@Numero='')
			BEGIN
				select p.id,p.numero,count(i.serial)cantidad,po.numero as posicion from Pallet p 
				left join Inventario i on p.Numero=i.pallet 
				left join Posicion po on p.posicionId=po.id 
				inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
				where e.nombre=@Estado and estadoInventario=1 and p.Activo =1 AND p.UsuarioId = @UsuarioId
				group by p.id,p.numero,po.numero
			END
			ELSE
			BEGIN
				select p.id,p.numero,count(i.serial)cantidad,po.numero as posicion from Pallet p 
				left join Inventario i on p.Numero=i.pallet 
				left join Posicion po on p.posicionId=po.id 
				inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
				where p.Numero=@Numero and e.nombre=@Estado and estadoInventario=1 and p.Activo =1 AND p.UsuarioId = @UsuarioId
				group by p.id,p.numero,po.numero
			END
		END
END
go

/**
 * Author:  Julian Agamez
 * Created: Jul, 16  2024
 */ 

/*
*SCRIPT CLIENTE
*/

CREATE PROCEDURE [dbo].[pa_SearchGeneralSettingsPallet]
@Numero varchar(20),
@Estado varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
 
	SET NOCOUNT ON;
 
	IF(@Estado = 'ALMACENAMIENTO')
		BEGIN
			IF(@Numero='')
			BEGIN
				select p.id,p.numero,count(i.serial)cantidad,po.numero as posicion from Pallet p 
				left join Ingreso i on p.id=i.palletId 
				left join Posicion po on p.posicionId=po.id 
				inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
				where e.nombre=@Estado and estadoInventario=0
				group by p.id,p.numero,po.numero
			END
			ELSE
			BEGIN
				select p.id,p.numero,count(i.serial)cantidad,po.numero as posicion from Pallet p 
				left join Ingreso i on p.id=i.palletId 
				left join Posicion po on p.posicionId=po.id 
				inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
				where p.Numero=@Numero and e.nombre=@Estado and estadoInventario=0
				group by p.id,p.numero,po.numero
			END
		END
	ELSE	
		BEGIN
			IF(@Numero='')
			BEGIN
				select p.id,p.numero,count(i.serial)cantidad,po.numero as posicion from Pallet p 
				left join Inventario i on p.Numero=i.pallet 
				left join Posicion po on p.posicionId=po.id 
				inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
				where estadoInventario=0
				group by p.id,p.numero,po.numero
			END
			ELSE
			BEGIN
				select p.id,p.numero,count(i.serial)cantidad,po.numero as posicion from Pallet p 
				left join Inventario i on p.Numero=i.pallet 
				left join Posicion po on p.posicionId=po.id 
				inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
				where p.Numero=@Numero and e.nombre=@Estado and estadoInventario=0
				group by p.id,p.numero,po.numero
			END
		END
END
go

/**
 * Author: Pedro Neira
 * Created: MAR, 14  2025
 */

CREATE PROCEDURE [dbo].[pa_SearchGroupAccesory]
@Codigo varchar(50),
@Estado varchar(20),
@Tipo varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Codigo='')
	BEGIN
		IF(@Tipo='')
		BEGIN
			select cs.codigo,cs.descripcion,count(a.id)cantidad from Accesorio a 
			inner join CodigoSap cs on a.codigoSapId=cs.id 
			inner join WmsWdGeneral.dbo.estado e on a.estadoId=e.id 
			where e.nombre=@Estado 
			group by cs.codigo,cs.descripcion
		END
		ELSE
		BEGIN
			select cs.codigo,cs.descripcion,count(a.id)cantidad from Accesorio a 
			inner join CodigoSap cs on a.codigoSapId=cs.id 
			inner join WmsWdGeneral.dbo.estado e on a.estadoId=e.id 
			where e.nombre=@Estado and a.TipoAccesorio=@Tipo 
			group by cs.codigo,cs.descripcion
		END
	END
	ELSE
	BEGIN
		IF(@Tipo='')
		BEGIN
            SELECT cs.codigo, cs.descripcion, (SELECT COUNT(a.id) 
                    FROM Accesorio a 
                    LEFT JOIN WmsWdGeneral.dbo.estado e ON a.estadoId = e.id 
                    WHERE a.codigoSapId = cs.id AND e.nombre = @Estado) AS cantidad
            FROM CodigoSap cs
            WHERE cs.Codigo = @Codigo;
		END
		ELSE
		BEGIN
            SELECT cs.codigo, cs.descripcion, (SELECT COUNT(a.id) 
                    FROM Accesorio a 
                    LEFT JOIN WmsWdGeneral.dbo.estado e ON a.estadoId = e.id 
                    WHERE a.codigoSapId = cs.id AND e.nombre = @Estado AND a.TipoAccesorio=@Tipo) AS cantidad
            FROM CodigoSap cs
            WHERE cs.Codigo = @Codigo;
		END
	END

END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchGroupPart]
@Referencia varchar(50),
@Parte varchar(50),
@Estado varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Referencia='')
	BEGIN
		IF(@Parte='')
		BEGIN
			select m.codigo as parte,count(p.id)cantidad,cs.codigo,cs.descripcion from Parte p 
			inner join Maestro m on p.parteId=m.id 
			inner join CodigoSap cs on p.codigoSapId=cs.id 
			inner join WmsWdGeneral.dbo.estado e on p.estadoId=e.id 
			where e.nombre=@Estado 
			group by m.codigo,cs.codigo,cs.descripcion
		END
		ELSE
		BEGIN
			select m.codigo as parte,count(p.id)cantidad,cs.codigo,cs.descripcion from Parte p 
			inner join Maestro m on p.parteId=m.id 
			inner join CodigoSap cs on p.codigoSapId=cs.id 
			inner join WmsWdGeneral.dbo.estado e on p.estadoId=e.id 
			where e.nombre=@Estado and m.codigo=@Parte 
			group by m.codigo,cs.codigo,cs.descripcion
		END
	END
	ELSE
	BEGIN
		IF(@Parte='')
		BEGIN
			select m.codigo as parte,count(p.id)cantidad,cs.codigo,cs.descripcion from Parte p 
			inner join Maestro m on p.parteId=m.id 
			inner join CodigoSap cs on p.codigoSapId=cs.id 
			inner join WmsWdGeneral.dbo.estado e on p.estadoId=e.id 
			where e.nombre=@Estado and cs.codigo=@Referencia 
			group by m.codigo,cs.codigo,cs.descripcion
		END
		ELSE
		BEGIN
			select m.codigo as parte,count(p.id)cantidad,cs.codigo,cs.descripcion from Parte p 
			inner join Maestro m on p.parteId=m.id 
			inner join CodigoSap cs on p.codigoSapId=cs.id 
			inner join WmsWdGeneral.dbo.estado e on p.estadoId=e.id 
			where e.nombre=@Estado and cs.codigo=@Referencia and m.codigo=@Parte 
			group by m.codigo,cs.codigo,cs.descripcion
		END
	END

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchLabel]
@Nombre VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Nombre='')
	BEGIN
		SELECT * FROM Etiqueta 
		ORDER BY Nombre
	END
	ELSE
	BEGIN
		SELECT * FROM Etiqueta 
		WHERE Nombre=@Nombre
	END

END



go


-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchLabelField]
@EtiquetaId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	select id,nombre,valor from EtiquetaCampo where EtiquetaId=@EtiquetaId

END




go


CREATE PROCEDURE [dbo].[pa_SearchLabeled]
    @nombre VARCHAR(50),
    @Tipo VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    IF(@Nombre = '')
    BEGIN
        SELECT *
        FROM Etiqueta
        WHERE Tipo = @Tipo
          AND activo = 1
        ORDER BY Nombre;
    END
    ELSE
    BEGIN
        SELECT *
        FROM Etiqueta
        WHERE Tipo = @Tipo
          AND activo = 1
          AND Nombre = @Nombre;
    END
END;
go


    CREATE PROCEDURE [dbo].[pa_SearchLabourClient]
        @Tipo VARCHAR(20),
        @ManoDeObraId INT
        AS
        BEGIN
        IF(@Tipo = 'UsuarioActivo')
                BEGIN    
                    set nocount on;
                    SELECT u.Id,u.nombres,u.apellidos,u.nombreUsuario,u.Identificacion
                    FROM WmsWdGeneral.dbo.Usuario u 
                    WHERE u.Id IN (Select usuarioId FROM ManoDeObraUsuario
                    WHERE manoDeobraId = @ManoDeObraId AND usuarioActivo = 1) 
                END
            ELSE IF (@Tipo = 'Altas')
                BEGIN
                    set nocount on;
                    SELECT u.Id,u.nombres,u.apellidos,u.nombreUsuario,u.Identificacion
                    FROM WmsWdGeneral.dbo.Usuario u 
                    WHERE u.Id IN (Select usuarioId FROM ManoDeObraUsuario
                    WHERE manoDeobraId = @ManoDeObraId AND altas = 1) 
                END
            ELSE IF (@Tipo = 'Bajas')
                BEGIN
                    set nocount on;
                    SELECT u.Id,u.nombres,u.apellidos,u.nombreUsuario,u.Identificacion
                    FROM WmsWdGeneral.dbo.Usuario u 
                    WHERE u.Id IN (Select usuarioId FROM ManoDeObraUsuario
                    WHERE manoDeobraId = @ManoDeObraId and bajas =1) 
                END
        END
go

CREATE PROCEDURE [dbo].[pa_SearchLevel]
    @IdNivel INT
    AS
    BEGIN

        SET NOCOUNT ON;
            SELECT m.Codigo FROM Maestro m 
	        INNER JOIN TipoMaestro tm ON m.TipoMaestroId=tm.id 
	        WHERE tm.Nombre='Niveles' AND m.Id = @IdNivel 
    END
go

CREATE PROCEDURE [dbo].[pa_SearchLevelComponentsAsig]
    @Serial VARCHAR(50)
    AS
    BEGIN

        SET NOCOUNT ON;
            SELECT  MAX(m.detalle) AS nivel FROM Maestro m
            INNER JOIN SerialComponente sc ON m.id = sc.ComponenteId
            WHERE sc.SerialId = (SELECT id FROM ingreso WHERE Serial = @Serial)
    END
go

CREATE PROCEDURE [dbo].[pa_SearchLevelId]
    @Nivel VARCHAR(50)
    AS
    BEGIN

        SET NOCOUNT ON;
            SELECT m.Id FROM Maestro m 
	        INNER JOIN TipoMaestro tm ON m.TipoMaestroId=tm.id 
	        WHERE tm.Nombre='Niveles' AND m.Codigo = @Nivel 
    END
go

CREATE PROCEDURE [dbo].[pa_SearchMandatoryComponent]
        @CodigoSapId INT,
        @FallaId INT
        AS
        BEGIN
            SELECT ComponenteObligatorio FROM CodigoSapFalla
            WHERE CodigoSapId = @CodigoSapId AND FallaId = @FallaId
        END
go

CREATE PROCEDURE [dbo].[pa_SearchManoDeObra] AS BEGIN SET NOCOUNT ON; SELECT m.id, m.fecha, m.detalle, m.observaciones, m.mano_obra_json, m.activo, m.usuario, m.created_at, m.updated_at FROM ManoDeObra m ORDER BY m.fecha DESC; END;
go


CREATE PROCEDURE [dbo].[pa_SearchMaster]
@TipoMaestro varchar(50)
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	SELECT m.id,m.codigo,m.descripcion,m.detalle,m.adicional,tm.nombre AS tipoMaestro,m.activo FROM Maestro m 
	INNER JOIN TipoMaestro tm on m.tipoMaestroId=tm.id 
	WHERE tm.nombre=@TipoMaestro 
	ORDER BY m.codigo

END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchMasterType]
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	set nocount on;

	select * from TipoMaestro order by nombre

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchModelsPallet]
@PalletId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	select cs.codigo,count(i.serial)cantidad from Ingreso i 
	inner join CodigoSap cs on i.codigoSapId=cs.id 
	where i.PalletId=@PalletId 
	group by cs.codigo

END




go


CREATE PROCEDURE [dbo].[pa_SearchNoveltyEntry]
@TipoNovedad varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT 
		i.id
		,CONCAT(i.serial,', ',i.Serial3,', ',i.Serial4,', ',i.Serial5) AS serial
		,i.mac
		,cs.codigo
		,cs.descripcion
		,i.novedad
		,i.observaciones 
	FROM Ingreso i 
	LEFT JOIN CodigoSap cs ON i.codigoSapId=cs.id 
	INNER JOIN Maestro t ON i.tipologiaId=t.id 
	INNER JOIN WmsWdGeneral.dbo.estado e ON i.estadoId=e.id 
	WHERE e.nombre='NOVEDAD' and t.codigo=@TipoNovedad

END
go

CREATE   PROCEDURE [dbo].[pa_SearchNoveltyEntryWeb] 

@Perfil VARCHAR(50),
@UsuarioId INT,
@TipoNovedad VARCHAR(50)
AS                                                                                                                                                                
BEGIN                                                                                                                                                             
 -- SET NOCOUNT ON added to prevent extra result sets from                                                                                                        
 -- interfering with SELECT statements.                                                                                                                           
 SET NOCOUNT ON;                                                                                                                                                  
 SELECT DISTINCT i.serial,i.mac,cs.codigo,cs.descripcion FROM Ingreso i                                                                                             
 INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id                                                                                                              
 INNER JOIN WmsWdGeneral.dbo.estado es ON i.estadoId=es.id                                                                                                       
 WHERE ((es.nombre='NOVEDAD-TERMINADO')) 
 AND (@Perfil = 'ADMINISTRADOR' OR (i.UsuarioIdMovimiento = @UsuarioId AND @Perfil <> 'ADMINISTRADOR')) 
 AND i.Novedad =@TipoNovedad
END
go


-- =============================================
-- Author:		MEDINA DAVID
-- ALTER date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchPackedAccesory]
@Estado varchar(20),
@Serial varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

SET NOCOUNT ON;

	select cs.codigo,cs.descripcion,isnull(count(a.id),0) as cantidad 
	into #TempAcceNuevo
	from Accesorio a 
	inner join CodigoSap cs on a.codigoSapId=cs.id 
	inner join dbo.estado e on a.estadoId=e.id 
	where e.nombre=@Estado and cs.Codigo in(select cs.codigo from Accesorio a inner join CodigoSap cs on a.CodigoSapId=cs.Id where a.SerialEmpaque=@Serial) and a.TipoAccesorio = 'Nuevo'
	group by cs.codigo,cs.descripcion

	select cs.codigo,cs.descripcion,isnull(count(a.id),0) as cantidad
	into #TempAcceReacon
	from Accesorio a 
	inner join CodigoSap cs on a.codigoSapId=cs.id 
	inner join dbo.estado e on a.estadoId=e.id 
	where e.nombre=@Estado and cs.Codigo in(select cs.codigo from Accesorio a inner join CodigoSap cs on a.CodigoSapId=cs.Id where a.SerialEmpaque=@Serial) and a.TipoAccesorio = 'Reacondicionado'
	group by cs.codigo,cs.descripcion

	Select ISNULL(a.Codigo,n.codigo)codigo, isnull(a.Descripcion,n.descripcion)as descripcion, isnull(n.cantidad,0) as Nuevos, isnull(a.cantidad,0) as Reacondicionados from #TempAcceNuevo n
	full outer join #TempAcceReacon a ON n.codigo = a.Codigo

	drop table #TempAcceNuevo
	drop table #TempAcceReacon
END







go

 
CREATE PROCEDURE [dbo].[pa_SearchPacking]
@PalletId INT
AS
 
BEGIN
 
	SET NOCOUNT ON;
 
    SELECT ce.id
    	,ce.numero
    	,count(ing.id)seriales 
    FROM CajaEmpaque ce 
	LEFT JOIN (
		select i.Id,i.CajaEmpaqueId FROM INGRESO i 
        INNER JOIN CodigoSap cs ON i.CodigoSapId = cs.Id
        INNER JOIn Maestro m ON cs.TipoId = m.Id
        WHERE m.Codigo = 'SERIALIZADOS'
    )ing ON  ce.id=ing.cajaEmpaqueId
	WHERE ce.palletId= @PalletId  
	GROUP BY ce.id, ce.numero 
	ORDER BY ce.Id;
END
go


-- =============================================
-- Author:		MEDINA DAVID
-- ALTER date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchPackingAccesory]
@Estado varchar(20),
@Serial varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

SET NOCOUNT ON;

	select cs.codigo,cs.descripcion,isnull(count(a.id),0) as cantidad 
	into #TempAcceNuevo
	from Accesorio a 
	inner join CodigoSap cs on a.codigoSapId=cs.id 
	inner join dbo.estado e on a.estadoId=e.id 
	where e.nombre=@Estado and cs.Codigo not in(select cs.codigo from Accesorio a inner join CodigoSap cs on a.CodigoSapId=cs.Id where a.SerialEmpaque=@Serial) and a.TipoAccesorio = 'Nuevo'
	group by cs.codigo,cs.descripcion

	select cs.codigo,cs.descripcion,isnull(count(a.id),0) as cantidad
	into #TempAcceReacon
	from Accesorio a 
	inner join CodigoSap cs on a.codigoSapId=cs.id 
	inner join dbo.estado e on a.estadoId=e.id 
	where e.nombre=@Estado and cs.Codigo not in(select cs.codigo from Accesorio a inner join CodigoSap cs on a.CodigoSapId=cs.Id where a.SerialEmpaque=@Serial) and a.TipoAccesorio = 'Reacondicionado'
	group by cs.codigo,cs.descripcion

	Select ISNULL(a.Codigo,n.codigo)codigo, isnull(a.Descripcion,n.descripcion)as descripcion, isnull(n.cantidad,0) as Nuevos, isnull(a.cantidad,0) as Reacondicionados from #TempAcceNuevo n
	full outer join #TempAcceReacon a ON n.codigo = a.Codigo

	drop table #TempAcceNuevo
	drop table #TempAcceReacon
END







go


CREATE PROCEDURE [dbo].[pa_SearchPackingCodigoSapAccesory]
	@Estado VARCHAR(20),
	@PalletId INT,
	@CodigoSapId INT
	AS
	BEGIN
	DECLARE @UseCodigoSapAccesorio BIT;
	SET NOCOUNT ON;
				
		IF (SELECT cs.AsignacionAcc 
			FROM pallet p 
			INNER JOIN codigoSap cs ON p.codigoSapId = cs.Id 
			WHERE p.Id = @PalletId) = 0
		BEGIN
			SET @UseCodigoSapAccesorio = 0;
		END
		ELSE
		BEGIN
			SET @UseCodigoSapAccesorio = 1;
		END;

		WITH Accesorios AS (
			SELECT 
				cs.codigo, 
				cs.descripcion, 
				m.Codigo AS familia, 
				a.TipoAccesorio,
				COUNT(a.id) AS cantidad
			FROM Accesorio a 
			INNER JOIN CodigoSap cs ON a.codigoSapId = cs.id 
			INNER JOIN dbo.estado e ON a.estadoId = e.id 
			INNER JOIN Maestro m ON cs.FamiliaId = m.Id
			LEFT JOIN CodigoSapAccesorio csa ON a.codigoSapId = csa.AccesorioId AND @UseCodigoSapAccesorio = 1
			WHERE e.nombre = @Estado
			AND (@UseCodigoSapAccesorio = 0 OR csa.codigosapId = @CodigoSapId)
			GROUP BY cs.codigo, cs.descripcion, m.Codigo, a.TipoAccesorio
		)
		, TempAcce AS (
			SELECT 
				codigo, 
				descripcion, 
				familia,
				SUM(CASE WHEN TipoAccesorio = 'Nuevo' THEN cantidad ELSE 0 END) AS Nuevos,
				SUM(CASE WHEN TipoAccesorio = 'Reacondicionado' THEN cantidad ELSE 0 END) AS Reacondicionados
			FROM Accesorios
			GROUP BY codigo, descripcion, familia
		)
		SELECT 
			codigo, 
			descripcion, 
			familia, 
			ISNULL(Nuevos, 0) AS Nuevos, 
			ISNULL(Reacondicionados, 0) AS Reacondicionados
		FROM TempAcce;

		DELETE FROM TempSeriales;
END;
go

CREATE PROCEDURE [dbo].[pa_SearchPackingDeliveryPallet]
@Numero varchar(20),
@Tipologia varchar(50),
@Tipo varchar(50)
AS 
BEGIN 
    SET NOCOUNT ON;
 
    IF(@Numero='')
    BEGIN
        select p.id,p.numero,count(i.serial)cantidad,o.nombre as origen,t.codigo as tipologia,cs.Codigo AS CodigoSap, cs.Descripcion  AS Descripcion  from Pallet p
        inner join Ingreso i on p.id=i.palletId
        inner join Maestro t on p.tipologiaId=t.id
        inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id
        inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id
        inner join CodigoSap cs ON i.CodigoSapId = cs.Id
        inner join Maestro m ON cs.TipoId = m.Id
        where (e.nombre ='EMPAQUE-ENTREGA') and t.codigo=@Tipologia and p.activo=1 AND m.Codigo = @Tipo
        group by p.id,p.numero,o.nombre,t.codigo, cs.Codigo, cs.Descripcion;
    END
    ELSE
    BEGIN
        select p.id,p.numero,count(i.serial)cantidad,o.nombre as origen,t.codigo as tipologia, cs.Codigo AS CodigoSap, cs.Descripcion  AS Descripcion from Pallet p
        inner join Ingreso i on p.id=i.palletId
        inner join Maestro t on p.tipologiaId=t.id
        inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id
        inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id
        inner join CodigoSap cs ON i.CodigoSapId = cs.Id
        inner join Maestro m ON cs.TipoId = m.Id
        where p.Numero=@Numero and (e.nombre ='EMPAQUE-ENTREGA') and t.codigo=@Tipologia and p.activo=1 AND m.Codigo = @Tipo
        group by p.id,p.numero,o.nombre,t.codigo, cs.Codigo, cs.Descripcion;
    END 
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchPalletBoxDispatchDeliveryPallet]
@Numero varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Numero='')
	BEGIN
		select p.id,p.numero,count(i.id)registros,o.nombre as origen,t.codigo as tipologia from Pallet p 
		full outer join Ingreso i on i.PalletId=p.id 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
		where e.nombre='PREDESPACHO' and o.nombre = 'EMPAQUE' 
		group by p.id,p.numero,o.nombre,t.codigo
	END
	ELSE
	BEGIN
		select p.id,p.numero,count(i.id)registros,o.nombre as origen,t.codigo as tipologia from Pallet p 
		full outer join Ingreso i on i.PalletId=p.id 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
		where p.Numero=@Numero and e.nombre='PREDESPACHO' and o.nombre = 'EMPAQUE' 
		group by p.id,p.numero,o.nombre,t.codigo
	END

END




go


CREATE PROCEDURE [dbo].[pa_SearchPalletBoxDispatchPallet]
@Numero varchar(20)
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Numero='')
	BEGIN
		select p.id,p.numero,cs.Codigo ,cs.Descripcion,COALESCE(COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END), 0) AS cajas,((SELECT ISNULL((Select COALESCE(count(i.PalletId),0) from pallet pp 
                                                                                                        inner join ingreso i on pp.id = i.PalletId
                                                                                                        inner join WmsWdGeneral.dbo.estado d on pp.destinoId=d.id
                                                                                                        inner join CodigoSap cs On i.CodigoSapId = cs.Id
                                                                                                        inner join Maestro m ON cs.TipoId = m.Id
                                                                                                        where d.nombre='DESPACHO' and pp.activo=1 and m.Codigo <> 'SMARTCARD'
                                                                                                        and pp.id = p.id
                                                                                                        group by i.PalletId),0)))AS 'Cantidad Equip.',(SELECT ISNULL((Select count(a.PalletId) from pallet pl 
                                                                            inner join accesorio a on pl.id = a.PalletId
                                                                            inner join WmsWdGeneral.dbo.estado d on pl.destinoId=d.id
                                                                            where d.nombre='DESPACHO' and pl.activo=1
                                                                            and pl.id = p.id
                                                                            group by a.PalletId),0)) AS 'Cantidad Acc.',((SELECT ISNULL((Select COALESCE(count(i.PalletId),0) from pallet pp 
                                                                                                        inner join ingreso i on pp.id = i.PalletId
                                                                                                        inner join WmsWdGeneral.dbo.estado d on pp.destinoId=d.id
                                                                                                        inner join CodigoSap cs On i.CodigoSapId = cs.Id
                                                                                                        inner join Maestro m ON cs.TipoId = m.Id
                                                                                                        where d.nombre='DESPACHO' and pp.activo=1 and m.Codigo = 'SMARTCARD'
                                                                                                        and pp.id = p.id
                                                                                                        group by i.PalletId),0)))AS 'Cantidad SmartC.',
        t.codigo as tipologia from Pallet p 
		left join CajaDespacho cd on p.id=cd.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.estado d on p.destinoId=d.id 
        inner join CodigoSap cs on p.CodigoSapId = cs.Id
		where d.nombre='DESPACHO' and p.activo=1 
		group by p.id,p.numero,cs.codigo,cs.Descripcion,t.codigo

	END
	ELSE
	BEGIN
		select p.id,p.numero,cs.Codigo ,cs.Descripcion,COALESCE(COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END), 0) AS cajas,((SELECT ISNULL((Select COALESCE(count(i.PalletId),0) from pallet pp 
                                                                                                        inner join ingreso i on pp.id = i.PalletId
                                                                                                        inner join WmsWdGeneral.dbo.estado d on pp.destinoId=d.id
                                                                                                        inner join CodigoSap cs On i.CodigoSapId = cs.Id
                                                                                                        inner join Maestro m ON cs.TipoId = m.Id
                                                                                                        where d.nombre='DESPACHO' and pp.activo=1 and m.Codigo <> 'SMARTCARD'
                                                                                                        and pp.id = p.id
                                                                                                        group by i.PalletId),0)))AS 'Cantidad Equip.',(SELECT ISNULL((Select count(a.PalletId) from pallet pl 
                                                                            inner join accesorio a on pl.id = a.PalletId
                                                                            inner join WmsWdGeneral.dbo.estado d on pl.destinoId=d.id
                                                                            where d.nombre='DESPACHO' and pl.activo=1
                                                                            and pl.id = p.id
                                                                            group by a.PalletId),0)) AS 'Cantidad Acc.',((SELECT ISNULL((Select COALESCE(count(i.PalletId),0) from pallet pp 
                                                                                                        inner join ingreso i on pp.id = i.PalletId
                                                                                                        inner join WmsWdGeneral.dbo.estado d on pp.destinoId=d.id
                                                                                                        inner join CodigoSap cs On i.CodigoSapId = cs.Id
                                                                                                        inner join Maestro m ON cs.TipoId = m.Id
                                                                                                        where d.nombre='DESPACHO' and pp.activo=1 and m.Codigo = 'SMARTCARD'
                                                                                                        and pp.id = p.id
                                                                                                        group by i.PalletId),0)))AS 'Cantidad SmartC.',
        t.codigo as tipologia from Pallet p 
		left join CajaDespacho cd on p.id=cd.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.estado d on p.destinoId=d.id 
        inner join CodigoSap cs on p.CodigoSapId = cs.Id 
		where p.Numero=@Numero and d.nombre='DESPACHO' and p.activo=1
		group by p.id,p.numero,cs.codigo,cs.Descripcion,t.codigo
	END

END
go

/**
 * Author:  Pedro Neira 
 * Created: SEP, 27  2024
 */ 

CREATE PROCEDURE [dbo].[pa_SearchPalletBoxEntry]
@Estado varchar(50),
@PalletId int,
@CajaEmpaqueId int
AS
 
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
 
    IF(@Estado='Empaque')
    BEGIN
        SELECT i.serial,i.mac,e.smartCard,(SELECT ISNULL((SELECT numeroSmartcard FROM Ingreso WHERE Id = e.smartCardId), '0'))
        as numeroSmartcard,i.Serial3,i.Serial4,cs.codigo,cs.descripcion,n.codigo AS nivel,l.nombre as Lote, md.Codigo as modelo FROM Ingreso i
        INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id
        INNER JOIN Maestro n ON i.nivelId=n.id
        INNER JOIN Empaque e ON i.Id = e.SerialId
        LEFT JOIN Lote l ON i.loteId =  l.id
        LEFT JOIN Maestro md ON i.modeloId = md.id
        WHERE i.palletId=@PalletId and i.cajaEmpaqueId=@CajaEmpaqueId
        ORDER BY i.fechaMovimiento
    END
    ELSE
    BEGIN
        SELECT i.serial,i.mac,e.smartCard,(SELECT ISNULL((SELECT numeroSmartcard FROM Ingreso WHERE Id = e.smartCardId), '0'))
        as numeroSmartcard,i.Serial3,i.Serial4,cs.codigo,cs.descripcion,n.codigo AS nivel,l.nombre as Lote, md.Codigo as modelo FROM Ingreso i
        INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id
        INNER JOIN Maestro n ON i.nivelId=n.id
        INNER JOIn Maestro m ON cs.TipoId = m.Id
        LEFT JOIN Empaque e ON i.Id = e.SerialId
        LEFT JOIN Lote l ON i.loteId =  l.id
         LEFT JOIN Maestro md ON i.modeloId = md.id
        WHERE i.palletId=@PalletId and i.cajaDespachoId=@CajaEmpaqueId and m.Codigo = 'SERIALIZADOS'
        ORDER BY i.fechaMovimiento
    END
 
END
go

CREATE PROCEDURE [dbo].[pa_SearchPalletBoxPallet]
@Numero varchar(20),
@Destino varchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Destino='PREDESPACHO')
	BEGIN
		IF(@Numero='')
		BEGIN
			SELECT p.id, p.Numero AS Numero, COALESCE(COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END), 0) AS cajas, t.codigo AS tipologia from Pallet p
			left join CajaDespacho cd on p.id=cd.palletId 
			inner join Maestro t on p.tipologiaId=t.id 
			inner join WmsWdGeneral.dbo.estado d on p.destinoId=d.id 
			where d.nombre=@Destino and p.activo=1 
			group by p.id,p.numero,t.codigo
		END
		ELSE
		BEGIN
			SELECT p.id,p.Numero AS Numero, COALESCE(COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END), 0) AS cajas, t.codigo AS tipologia from Pallet p 
			left join CajaDespacho cd on p.id=cd.palletId 
			inner join Maestro t on p.tipologiaId=t.id 
			inner join WmsWdGeneral.dbo.estado d on p.destinoId=d.id 
			where p.Numero=@Numero and d.nombre=@Destino and p.activo=1 
			group by p.id,p.numero,t.codigo
		END
	END
	ELSE
	BEGIN
		IF(@Numero='')
		BEGIN
			select p.id,p.numero as Numero,count(ce.id)cajas,right(it.descripcion,11) AS Descripcion,t.codigo as tipologia from Pallet p 
			left join CajaEmpaque ce on p.id=ce.palletId 
			inner join Maestro t on p.tipologiaId=t.id
			inner join Codigosap it on p.codigosapid=it.id
			inner join WmsWdGeneral.dbo.estado d on p.destinoId=d.id 
			where d.nombre=@Destino and
			p.activo=1 
			group by p.id,p.numero,t.codigo,it.descripcion
		END
		ELSE
		BEGIN
			select p.id,p.numero as Numero,count(ce.id)cajas,right(it.descripcion,11) AS Descripcion,t.Codigo as tipologia from Pallet p 
			left join CajaEmpaque ce on p.id=ce.palletId 
			inner join Maestro t on p.tipologiaId=t.id 
			inner join Codigosap it on p.codigosapid=it.id
			inner join WmsWdGeneral.dbo.estado d on p.destinoId=d.id 
		    where p.Numero=@Numero and d.nombre=@Destino and p.activo=1 
			group by p.id,p.numero,t.codigo,it.descripcion
		END
	END

END;

go

CREATE    PROCEDURE [dbo].[pa_SearchPalletBoxPalletWeb]
@Numero varchar(20),
@Destino varchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
 
	SET NOCOUNT ON;
 
	IF(@Destino='PREDESPACHO')
	BEGIN
		IF(@Numero='')
		BEGIN
			SELECT p.id, p.Numero AS Numero, COALESCE(COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END), 0) AS cajas,it.descripcion AS Descripcion, t.codigo AS tipologia from Pallet p
			left join CajaDespacho cd on p.id=cd.palletId
			inner join Maestro t on p.tipologiaId=t.id
			inner join Codigosap it on p.codigosapid=it.id
			inner join WmsWdGeneral.dbo.estado d on p.destinoId=d.id
			where d.nombre=@Destino and p.activo=1
			group by p.id,p.numero,t.codigo,it.descripcion
			ORDER BY p.id DESC
		END
		ELSE
		BEGIN
			SELECT p.id,p.Numero AS Numero, COALESCE(COUNT(CASE WHEN cd.Activo = 1 THEN cd.id END), 0) AS cajas,it.descripcion AS Descripcion, t.codigo AS tipologia from Pallet p
			left join CajaDespacho cd on p.id=cd.palletId
			inner join Maestro t on p.tipologiaId=t.id
			inner join Codigosap it on p.codigosapid=it.id
			inner join WmsWdGeneral.dbo.estado d on p.destinoId=d.id
			where p.Numero=@Numero and d.nombre=@Destino and p.activo=1
			group by p.id,p.numero,t.codigo,it.descripcion
		END
	END
	ELSE
	BEGIN
		IF(@Numero='')
		BEGIN
			select p.id,p.numero as Numero,count(ce.id)cajas,it.descripcion AS Descripcion,t.Codigo as tipologia from Pallet p
			left join CajaEmpaque ce on p.id=ce.palletId
			inner join Maestro t on p.tipologiaId=t.id
			inner join Codigosap it on p.codigosapid=it.id
			inner join WmsWdGeneral.dbo.estado d on p.destinoId=d.id
			where d.nombre=@Destino and
			p.activo=1
			group by p.id,p.numero,t.codigo,it.descripcion
			ORDER BY p.id DESC
		END
		ELSE
		BEGIN
			select p.id,p.numero as Numero,count(ce.id)cajas,it.descripcion AS Descripcion,t.Codigo as tipologia from Pallet p
			left join CajaEmpaque ce on p.id=ce.palletId
			inner join Maestro t on p.tipologiaId=t.id
			inner join Codigosap it on p.codigosapid=it.id
			inner join WmsWdGeneral.dbo.estado d on p.destinoId=d.id
		    where p.Numero=@Numero and d.nombre=@Destino and p.activo=1
			group by p.id,p.numero,t.codigo,it.descripcion
		END
	END
 
END
go

CREATE PROCEDURE [dbo].[pa_SearchPalletBoxValidate]
    @Estado varchar(50),
    @PalletId int,
    @CajaEmpaqueId int = 0
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        -- Usamos ISNULL para que el serial nunca llegue nulo al API
        ISNULL(i.serial, e.Serial) as serial,
        ISNULL(i.mac, 'N/A') as mac,
        ISNULL(e.smartCard, 'N/A') as smartCard,
        ISNULL(i_sc.numeroSmartcard, '0') as numeroSmartcard,
        ISNULL(i.Serial3, 'N/A') as Serial3,
        ISNULL(i.Serial4, 'N/A') as Serial4,
        ISNULL(cs.codigo, 'SIN MAESTRO') as codigoSap,
        ISNULL(cs.descripcion, 'No registrado en Ingreso') as descripcionSap,
        ISNULL(n.codigo, 'N/A') AS nivel,
        ISNULL(l.nombre, 'N/A') as lote, 
        ISNULL(md.Codigo, 'N/A') as modelo,
        
        -- VALIDACIÓN CRÍTICA
        CASE 
            WHEN i.serial IS NOT NULL AND e.SerialId IS NOT NULL THEN 1 -- Correcto
            ELSE 0 -- Si falta en algún lado, es 0
        END AS existeEnEmpaque,

        -- Para depuración visual
        CASE 
            WHEN i.serial IS NOT NULL AND e.SerialId IS NULL THEN 'Falta en Empaque'
            WHEN i.serial IS NULL AND e.SerialId IS NOT NULL THEN 'Sobra en Empaque'
            ELSE 'OK'
        END AS Observacion,

        ISNULL(p_ing.numero, 'N/A') as numPalletIngreso,
        ISNULL(ce_ing.numero, 'N/A') as numCajaIngreso,
        ISNULL(p_emp.numero, 'N/A') as numPalletEmpaque,
        ISNULL(ce_emp.numero, 'N/A') as numCajaEmpaque

    FROM (
        SELECT * FROM Ingreso 
        WHERE palletId = @PalletId 
        AND (cajaEmpaqueId = @CajaEmpaqueId OR @CajaEmpaqueId = 0 OR @CajaEmpaqueId IS NULL)
    ) i
    FULL OUTER JOIN (
        SELECT * FROM Empaque 
        WHERE PalletId = @PalletId 
        AND (CajaEmpaqueId = @CajaEmpaqueId OR @CajaEmpaqueId = 0 OR @CajaEmpaqueId IS NULL)
    ) e ON i.Id = e.SerialId 

    -- Joins de catálogo
    LEFT JOIN CodigoSap cs ON i.codigoSapId = cs.id
    LEFT JOIN Maestro n ON i.nivelId = n.id
    LEFT JOIN Lote l ON i.loteId = l.id
    LEFT JOIN Maestro md ON i.modeloId = md.id
    LEFT JOIN Ingreso i_sc ON e.smartCardId = i_sc.Id
    LEFT JOIN Pallet p_ing ON i.palletId = p_ing.id
    LEFT JOIN CajaEmpaque ce_ing ON i.cajaEmpaqueId = ce_ing.id
    LEFT JOIN Pallet p_emp ON e.PalletId = p_emp.id
    LEFT JOIN CajaEmpaque ce_emp ON e.CajaEmpaqueId = ce_emp.id
    
    ORDER BY Observacion DESC, i.fechaMovimiento;
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchPart]
@Parte varchar(20),
@Estado varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	IF(@Parte='')
	BEGIN
		select p.id,m.codigo as parte,cs.Codigo,cs.Descripcion from Parte p 
		inner join CodigoSap cs on p.CodigoSapId=cs.id 
		inner join Maestro m on p.parteId=m.id 
		inner join WmsWdGeneral.dbo.estado e on p.estadoId=e.id 
		where e.nombre=@Estado
	END
	ELSE
	BEGIN
		select p.id,m.codigo as parte,cs.Codigo,cs.Descripcion from Parte p 
		inner join CodigoSap cs on p.CodigoSapId=cs.id 
		inner join Maestro m on p.parteId=m.id 
		inner join WmsWdGeneral.dbo.estado e on p.estadoId=e.id 
		where e.nombre=@Estado and m.codigo=@Parte
	END

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchPreDispatch]
@PalletId INT
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_SearchPreDispatch @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    SELECT id,numero FROM CajaEmpaque 
	WHERE palletId=@PalletId

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchPreDispatchPallet]
@Numero varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Numero='')
	BEGIN
		select p.id,p.numero,count(ce.Id)cajas,o.nombre as origen,t.codigo as tipologia from Pallet p 
		inner join CajaEmpaque ce on ce.PalletId=p.id 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
		where e.nombre='PREDESPACHO' and o.nombre = 'EMPAQUE' 
		group by p.id,p.numero,o.nombre,t.codigo
	END
	ELSE
	BEGIN
		select p.id,p.numero,count(ce.Id)cajas,o.nombre as origen,t.codigo as tipologia from Pallet p 
		inner join CajaEmpaque ce on ce.PalletId=p.id 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
		where p.Numero=@Numero and e.nombre='PREDESPACHO' and o.nombre = 'EMPAQUE' 
		group by p.id,p.numero,o.nombre,t.codigo
	END

END




go


CREATE PROCEDURE [dbo].[pa_SearchPrealert]
@Nombre varchar(50)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    SET NOCOUNT ON;
	IF(@Nombre='')
	BEGIN
		select p.id,p.nombre,t.codigo as tipoOrigen,o.codigo as origen,p.guia,p.activo,p.Estado,ur.Nombres + ' ' + ur.Apellidos Responsable from Prealerta p 
		inner join Maestro t on p.tipoOrigenId=t.id 
		inner join Maestro o on p.origenId=o.id
		inner join WmsWdGeneral.dbo.Usuario ur on p.IdResponsable = ur.Id
		order by p.nombre
	END
	ELSE
	BEGIN
		select p.id,p.nombre,t.codigo as tipoOrigen,o.codigo as origen,p.guia,p.activo,p.Estado,ur.Nombres + ' ' + ur.Apellidos Responsable from Prealerta p 
		inner join Maestro t on p.tipoOrigenId=t.id 
		inner join Maestro o on p.origenId=o.id 
		inner join WmsWdGeneral.dbo.Usuario ur on p.IdResponsable = ur.Id
		where p.Nombre=@Nombre 
		order by p.nombre
	END
	

END

go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchPrealertSerial]
@PrealertaId int
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	SET NOCOUNT ON;

	select serial,mac,codigoSap,descripcion,cantidad,caja,falla,tecnicoCliente,pedido,tramite,novedad,garantia from PrealertaSerial 
	where prealertaId=@PrealertaId

END




go


    CREATE PROCEDURE [dbo].[pa_SearchPresupuesto]
        @Presupuesto varchar(50)
        AS

        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.
            
            set nocount on;

            IF(@Presupuesto='')
            BEGIN
                SELECT p.id,p.presupuesto,p.fecha,s.Codigo as segmento,n.codigo as nivel,p.unidades,p.reclamo,p.diasHabiles,p.tipo,p.detalle,p.observaciones
                FROM Presupuesto p
                LEFT JOIN Maestro s ON p.segmentoId = s.Id
                LEFT JOIN Maestro n ON p.nivelId = n.Id
                ORDER BY p.fecha
            
            END
            ELSE
            BEGIN
                SELECT p.id,p.presupuesto,p.fecha,s.Codigo as segmento,n.codigo as nivel,p.unidades,p.reclamo,p.diasHabiles,p.tipo,p.detalle,p.observaciones
                FROM Presupuesto p
                LEFT JOIN Maestro s ON p.segmentoId = s.Id
                LEFT JOIN Maestro n ON p.nivelId = n.Id
                WHERE p.presupuesto = @Presupuesto
                ORDER BY p.fecha
            END
        END
go


-- =============================================
-- Author:		MEDINA DAVID
-- ALTER date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchProcessAccesory]
@EstadoLimpieza varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@EstadoLimpieza='')
	BEGIN
		select a.id,cs.codigo,cs.descripcion,el.codigo as estadoLimpieza from Accesorio a 
		inner join CodigoSap cs on a.codigoSapId=cs.id 
		inner join Maestro el on a.EstadoLimpiezaId=el.Id 
		inner join WmsWdGeneral.dbo.estado e on a.estadoId=e.id 
		where e.nombre='LIMPIEZA-TERMINADO'
	END
	ELSE
	BEGIN
		select a.id,cs.codigo,cs.descripcion,el.codigo as estadoLimpieza from Accesorio a 
		inner join CodigoSap cs on a.codigoSapId=cs.id 
		inner join Maestro el on a.EstadoLimpiezaId=el.Id 
		inner join WmsWdGeneral.dbo.estado e on a.estadoId=e.id 
		where e.nombre='LIMPIEZA-TERMINADO' and el.Codigo=@EstadoLimpieza 
	END	

END







go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchProcessBoxPacking]
@PalletId INT,
@CajaEmpaqueId INT,
@Estado VARCHAR(50)
AS
/*
DECLARE @PalletId INT
DECLARE @CajaEmpaqueId INT
DECLARE @Estado VARCHAR(50)
SET @PalletId=0
SET @CajaEmpaqueId=0
SET @Estado=''
EXEC pa_SearchProcessBoxPacking @PalletId,@CajaEmpaqueId,@Estado
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    IF(@PalletId=0)
	BEGIN
		IF(@CajaEmpaqueId=0)
		BEGIN
			SELECT ce.id,ce.numero,count(i.id)seriales,e.nombre AS estado,p.numero AS pallet FROM CajaEmpaque ce 
			FULL OUTER JOIN Ingreso i ON ce.id=i.cajaEmpaqueId 
			INNER JOIN Pallet p ON ce.palletId=p.id 
			INNER JOIN WmsWdGeneral.dbo.estado e ON ce.estadoId=e.id 
			WHERE e.nombre=@Estado and ce.activo=1 
			GROUP BY ce.id,ce.numero,e.nombre,p.numero 
			ORDER BY ce.numero
		END
		ELSE
		BEGIN
			SELECT ce.id,ce.numero,count(i.id)seriales,e.nombre AS estado,p.numero AS pallet FROM CajaEmpaque ce 
			FULL OUTER JOIN Ingreso i ON ce.id=i.cajaEmpaqueId 
			INNER JOIN Pallet p ON ce.palletId=p.id 
			INNER JOIN WmsWdGeneral.dbo.estado e ON ce.estadoId=e.id 
			WHERE e.nombre=@Estado and ce.Id=@CajaEmpaqueId and ce.activo=1 
			GROUP BY ce.id,ce.numero,e.nombre,p.numero 
			ORDER BY ce.numero
		END
	END
	ELSE
	BEGIN
		IF(@CajaEmpaqueId=0)
		BEGIN
			SELECT ce.id,ce.numero,count(i.id)seriales,e.nombre AS estado,p.numero AS pallet FROM CajaEmpaque ce 
			FULL OUTER JOIN Ingreso i ON ce.id=i.cajaEmpaqueId 
			INNER JOIN Pallet p ON ce.palletId=p.id 
			INNER JOIN WmsWdGeneral.dbo.estado e ON ce.estadoId=e.id 
			WHERE ce.palletId=@PalletId and e.nombre=@Estado and ce.activo=1 
			GROUP BY ce.id,ce.numero,e.nombre,p.numero 
			ORDER BY ce.numero
		END
		ELSE
		BEGIN
			SELECT ce.id,ce.numero,count(i.id)seriales,e.nombre AS estado,p.numero AS pallet FROM CajaEmpaque ce 
			FULL OUTER JOIN Ingreso i ON ce.id=i.cajaEmpaqueId 
			INNER JOIN Pallet p ON ce.palletId=p.id 
			INNER JOIN WmsWdGeneral.dbo.estado e ON ce.estadoId=e.id 
			WHERE ce.palletId=@PalletId and e.nombre=@Estado and ce.Numero=@CajaEmpaqueId and ce.activo=1 
			GROUP BY ce.id,ce.numero,e.nombre,p.numero 
			ORDER BY ce.numero
		END
	END

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchProcessPart]
@EstadoCosmetica varchar(20),
@Parte varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Parte='' AND @EstadoCosmetica !='')
	BEGIN
		select p.id,m.codigo as parte,e.codigo as estado,cs.codigo as codigosap,cs.descripcion from parte p 
		inner join maestro m on p.parteid=m.id 
		inner join wmswdgeneral.dbo.estado es on p.estadoid=es.id 
		inner join maestro e on p.estadocosmeticaid=e.id 
		inner join CodigoSap cs on p.codigosapid=cs.id 
		where es.nombre='COSMETICA-PROCESO' and e.codigo=@EstadoCosmetica
	END
	ELSE
	IF(@Parte !='' AND @EstadoCosmetica !='')
	BEGIN
		select p.id,m.codigo as parte,e.codigo as estado,cs.codigo as codigosap,cs.descripcion from parte p 
		inner join maestro m on p.parteid=m.id 
		inner join wmswdgeneral.dbo.estado es on p.estadoid=es.id 
		inner join maestro e on p.estadocosmeticaid=e.id 
		inner join CodigoSap cs on p.codigosapid=cs.id 
		where es.nombre='COSMETICA-PROCESO' and e.codigo=@EstadoCosmetica and m.codigo=@Parte
	END
	ELSE
	IF(@Parte ='' AND @EstadoCosmetica='')
	BEGIN
		select p.id,m.codigo as parte,e.codigo as estado,cs.codigo as codigosap,cs.descripcion from parte p 
		inner join maestro m on p.parteid=m.id 
		inner join wmswdgeneral.dbo.estado es on p.estadoid=es.id 
		inner join maestro e on p.estadocosmeticaid=e.id 
		inner join CodigoSap cs on p.codigosapid=cs.id 
		where es.nombre='COSMETICA-PROCESO'
	END

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchProductSapCode]
@Codigo VARCHAR(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_SearchProductSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    IF(@Codigo='')
	BEGIN
		SELECT cs.id,cs.codigo,cs.Descripcion,f.Codigo as familia,stock=(SELECT count(i.Serial) FROM Ingreso i 
		INNER JOIN WmsWdGeneral.dbo.Estado e ON i.EstadoId=e.id 
		WHERE i.CodigoSapId=cs.id AND e.Nombre='ALMACENAMIENTO') FROM CodigoSap cs 
		LEFT JOIN Maestro f ON cs.FamiliaId=f.id 
		GROUP BY cs.id,cs.Codigo,cs.Descripcion,f.Codigo
	END
	ELSE
	BEGIN
		SELECT cs.id,cs.codigo,cs.Descripcion,f.Codigo as familia,stock=(SELECT count(i.Serial) FROM Ingreso i 
		INNER JOIN WmsWdGeneral.dbo.Estado e ON i.EstadoId=e.id 
		WHERE i.CodigoSapId=cs.id AND e.Nombre='ALMACENAMIENTO') FROM CodigoSap cs 
		LEFT JOIN Maestro f ON cs.FamiliaId=f.id 
		WHERE cs.Codigo=@Codigo 
		GROUP BY cs.id,cs.Codigo,cs.Descripcion,f.Codigo
	END

END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchQualityDeliveryPallet]
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	select p.id,p.numero,count(ce.id)cajas from Pallet p 
	left join CajaEmpaque ce on p.id=ce.palletId 
	inner join WmsWdGeneral.dbo.estado d on p.destinoId=d.id 
	where d.nombre='CALIDAD-ENTREGA' and p.activo=1 
	group by p.id,p.numero

END




go

CREATE PROCEDURE [dbo].[pa_SearchQualityEntry]
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT i.serial,i.mac,p.numero AS pallet,ce.numero AS caja,i.SmartCard FROM Ingreso i 
	INNER JOIN Pallet p ON i.palletId=p.id 
	INNER JOIN CajaEmpaque ce ON i.cajaEmpaqueId=ce.id 
	INNER JOIN WmsWdGeneral.dbo.estado e ON i.estadoId=e.id 
	WHERE e.nombre='CALIFICADO'

END;
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchReceivePacking]
@Estado VARCHAR(50),
@Pallet VARCHAR(20)
AS
/*
DECLARE @Estado VARCHAR(50)
DECLARE @Pallet VARCHAR(20)
SET @Estado = '001'
SET @Pallet = '001'
EXEC pa_SearchReceivePacking @Estado,@Pallet
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    IF(@Pallet='')
	BEGIN
		SELECT ce.id
			,ce.numero
			,p.numero AS pallet
			,count(i.serial)cantidad 
		FROM CajaEmpaque ce 
		INNER JOIN Ingreso i ON ce.id=i.cajaEmpaqueId 
		INNER JOIN Pallet p ON ce.palletId=p.id 
		INNER JOIN WmsWdGeneral.dbo.estado e on ce.estadoId=e.id 
		WHERE e.nombre=@Estado 
		GROUP BY ce.id,ce.numero,p.numero;
	END
	ELSE
	BEGIN
		SELECT 
			ce.id
			,ce.numero
			,p.numero AS pallet
			,count(i.serial)cantidad 
		FROM CajaEmpaque ce 
		INNER JOIN Ingreso i ON ce.id=i.cajaEmpaqueId 
		INNER JOIN Pallet p ON ce.palletId=p.id 
		INNER JOIN WmsWdGeneral.dbo.estado e on ce.estadoId=e.id 
		WHERE e.nombre=@Estado AND p.Numero=@Pallet 
		GROUP BY ce.id,ce.numero,p.numero;
	END

END




go


CREATE PROCEDURE [dbo].[pa_SearchReceivePallet]
@Numero varchar(20),
@Destino varchar(50),
@Tipo varchar(20)
AS

BEGIN
 
SET NOCOUNT ON;

IF(@Numero='')
    BEGIN
        IF(@Tipo='PALLETS')
  
          BEGIN
                select p.id,p.numero,count(i.serial)cantidad,o.nombre as origen,t.codigo as tipologia,cs.Codigo as CodigoSap, cs.Descripcion as Descripcion from Pallet p
                inner join Ingreso i on p.id=i.palletId
             
   inner join Maestro t on p.tipologiaId=t.id
                inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id
                inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id
                inner join CodigoSap cs ON p.CodigoSapId = cs.Id

                inner join Maestro m ON cs.TipoId = m.Id
                where e.nombre=@Destino and p.activo=1  AND m.Codigo ='SERIALIZADOS'
                group by p.id,p.numero,o.nombre,t.codigo,cs.Codigo,cs.Descripcion
            END
        ELSE IF(@Tipo='SMARTCARD')
            BEGIN
                select p.id,p.numero,count(i.serial)cantidad,o.nombre as origen,t.codigo as tipologia,cs.Codigo as CodigoSap, cs.Descripcion as Descripcion  from Pallet p
                inner join Ingreso i on p.id=i.palletId
                inner join Maestro t on p.tipologiaId=t.id
                inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id
                inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id
                inner join CodigoSap cs ON p.CodigoSapId = cs.Id
                inner join Maestro m ON cs.TipoId = m.Id
                where e.nombre=@Destino and p.activo=1  AND m.Codigo ='SMARTCARD'
                group by p.id,p.numero,o.nombre,t.codigo,cs.Codigo,cs.Descripcion
         
   END
        ELSE
            BEGIN
                select p.id,p.numero,count(a.Id)cantidad,o.nombre as origen,t.codigo as tipologia,cs.Codigo as CodigoSap, cs.Descripcion as Descripcion  from Pallet p
                inner join Accesorio a on p.id=a.palletId
                inner join Maestro t on p.tipologiaId=t.id
                inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id
                inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id
				inner join CodigoSap cs ON p.CodigoSapId = cs.Id
                where e.nombre=@Destino and p.activo=1
                group by p.id,p.numero,o.nombre,t.codigo,cs.Codigo,cs.Descripcion
            END
    END
ELSE
    BEGIN
        IF(@Tipo='PALLETS')
            BEGIN
                select p.id,p.numero,count(i.serial)cantidad,o.nombre as origen,t.codigo as tipologia,cs.Codigo as CodigoSap, cs.Descripcion as Descripcion  from Pallet p
                inner join Ingreso i on p.id=i.palletId
                inner join Maestro t on p.tipologiaId=t.id
                inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id
                inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id
                inner join CodigoSap cs ON p.CodigoSapId = cs.Id
                inner join Maestro m ON cs.TipoId = m.Id
                where p.Numero=@Numero and e.nombre=@Destino and p.activo=1 AND m.Codigo ='SERIALIZADOS'
                group by p.id,p.numero,o.nombre,t.codigo,cs.Codigo,cs.Descripcion
            END
        ELSE IF(@Tipo='SMARTCARD')
            BEGIN
                select p.id,p.numero,count(i.serial)cantidad,o.nombre as origen,t.codigo as tipologia,cs.Codigo as CodigoSap, cs.Descripcion as Descripcion  from Pallet p
                inner join Ingreso i on p.id=i.palletId
       
         inner join Maestro t on p.tipologiaId=t.id
                inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id
                inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id
                inner join CodigoSap cs ON p.CodigoSapId = cs.Id
                inner join Maestro m ON cs.TipoId = m.Id
                where p.Numero=@Numero and e.nombre=@Destino and p.activo=1 AND m.Codigo ='SMARTCARD'
        group by p.id,p.numero,o.nombre,t.codigo,cs.Codigo,cs.Descripcion
            END

        ELSE
            BEGIN
                select p.id,p.numero,count(a.Id)cantidad,o.nombre as origen,t.codigo as tipologia,cs.Codigo as CodigoSap, cs.Descripcion as Descripcion  from Pallet p
                inner join Accesorio a on p.id=a.palletId
                inner join Maestro t on p.tipologiaId=t.id
                inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id
                inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id
				inner join CodigoSap cs ON p.CodigoSapId = cs.Id
                where p.Numero=@Numero and e.nombre=@Destino and p.activo=1
                group by p.id,p.numero,o.nombre,t.codigo,cs.Codigo,cs.Descripcion
            END    
    END
END;
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchReceivePartsPallet]
@Numero varchar(20),
@Destino varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Numero='')
	BEGIN
		select p.id,p.numero,count(pa.id)cantidad,o.nombre as origen,t.codigo as tipologia from Pallet p 
		inner join Parte pa on p.id=pa.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
		where e.nombre=@Destino and p.activo=1 
		group by p.id,p.numero,o.nombre,t.codigo
	END
	ELSE
	BEGIN
		select p.id,p.numero,count(pa.id)cantidad,o.nombre as origen,t.codigo as tipologia from Pallet p 
		inner join Parte pa on p.id=pa.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
		where p.Numero=@Numero and e.nombre=@Destino and p.activo=1 
		group by p.id,p.numero,o.nombre,t.codigo
	END

END




go

-----------------------------------------------------------------------------------------------------------------------

CREATE   PROCEDURE [dbo].[pa_SearchRepairEntry]
@EstadoFinal varchar(20),
@Perfil VARCHAR(50),
@UsuarioId INT
AS
 
BEGIN
    SET NOCOUNT ON;
 
    SELECT DISTINCT i.Serial, i.Mac, cs.Codigo, cs.Descripcion, e.Codigo AS Estado
    FROM Ingreso i
    CROSS APPLY (SELECT TOP 1 r2.Id, r2.EstadoFinalId FROM Reparacion r2 WHERE r2.SerialId = i.Id ORDER BY r2.Id DESC) ult
    INNER JOIN CodigoSap cs ON i.CodigoSapId = cs.Id
    INNER JOIN Maestro e ON ult.EstadoFinalId = e.Id
    INNER JOIN WmsWdGeneral.dbo.Estado es ON i.EstadoId = es.Id
    WHERE es.Nombre = 'REPARACION-TERMINADO' 
      AND e.Codigo = @EstadoFinal
      AND (@Perfil = 'ADMINISTRADOR' OR @Perfil = 'ASIGNADOR REP' OR (i.UsuarioIdMovimiento = @UsuarioId AND @Perfil <> 'ADMINISTRADOR'))
    GROUP BY i.Serial, i.Mac, cs.Codigo, cs.Descripcion, e.Codigo;
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchRepairedRepair]
@TecnicoReparacionId int,
@Estado varchar(20)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	SET NOCOUNT ON;

	select r.serial,MAX(convert(date,r.fechaReparacion))fechaReparacion from Reparacion r 
	inner join Ingreso i on r.serialId=i.id 
	inner join Maestro m on r.estadoFinalId=m.id 
	inner join WmsWdGeneral.dbo.Estado e on i.estadoId=e.id 
	where m.codigo=@Estado and e.nombre='REPARACION' and r.tecnicoReparacionId=@TecnicoReparacionId 
	group by r.serial

END




go

CREATE PROCEDURE [dbo].[pa_SearchSapCode]
@Codigo VARCHAR(20)
AS
 
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
 
	SET NOCOUNT ON;
 
	IF(@Codigo='')
	BEGIN
		SELECT cs.id,cs.codigo,cs.descripcion,f.codigo AS familia,cs.validacion,cs.direccion,cs.largos,cs.recorte,cs.reingreso,c.codigo AS clasificacion,cs.numSerial,cs.activo,t.codigo AS tipo,
		CASE
			WHEN cs.AsignacionAcc = 0 THEN 'NO'
			WHEN cs.AsignacionAcc = 1 THEN 'SI'
        END AS Asignacion,
		CASE
			WHEN cs.AsignacionFalla = 0 or cs.AsignacionFalla is null THEN 'NO'
			WHEN cs.AsignacionFalla = 1 THEN 'SI'
        END AS AsignacionFa,cs.LargosMac,cs.LargosSerial3,cs.LargosSerial4,cs.LargosSerial5,te.Codigo as tipoEquipo, a.Codigo as area,m.Codigo as modelo,p.Codigo as proveedor,cs.validacionMac,cs.direccionMac,cs.recorteMac,cs.multimodelo,cs.cantidadCaja,cs.smartcard FROM CodigoSap cs 
		INNER JOIN Maestro f ON cs.familiaId=f.id 
		INNER JOIN Maestro c ON cs.clasificacionId=c.id 
		INNER JOIN Maestro t ON cs.tipoId=t.id 
		LEFT JOIN Maestro te ON cs.tipoEquipoId = te.Id
		LEFT JOIN Maestro a ON cs.areaId = a.Id
		LEFT JOIN Maestro m ON cs.modeloId = m.Id
		LEFT JOIN Maestro p ON cs.proveedorId = p.Id
		ORDER BY cs.codigo
	END
	ELSE
	BEGIN
		SELECT cs.id,cs.codigo,cs.descripcion,f.codigo AS familia,cs.validacion,cs.direccion,cs.largos,cs.recorte,cs.reingreso,c.codigo AS clasificacion,cs.numSerial,cs.activo,t.codigo AS tipo, 
		CASE
			WHEN cs.AsignacionAcc = 0 THEN 'NO'
			WHEN cs.AsignacionAcc = 1 THEN 'SI'
        END AS Asignacion,
		CASE
			WHEN cs.AsignacionFalla = 0 or cs.AsignacionFalla is null THEN 'NO'
			WHEN cs.AsignacionFalla = 1 THEN 'SI'
        END AS AsignacionFa, cs.LargosMac,cs.LargosSerial3,cs.LargosSerial4,cs.LargosSerial5,te.Codigo as tipoEquipo, a.Codigo as area,m.Codigo as modelo,p.Codigo as proveedor,cs.validacionMac,cs.direccionMac,cs.recorteMac,cs.multimodelo,cs.cantidadCaja,cs.smartcard FROM CodigoSap cs 
		INNER JOIN Maestro f ON cs.familiaId=f.id 
		INNER JOIN Maestro c ON cs.clasificacionId=c.id 
		INNER JOIN Maestro t ON cs.tipoId=t.id 
		LEFT JOIN Maestro te ON cs.tipoEquipoId = te.Id
		LEFT JOIN Maestro a ON cs.areaId = a.Id
		LEFT JOIN Maestro m ON cs.modeloId = m.Id
		LEFT JOIN Maestro p ON cs.proveedorId = p.Id
		WHERE cs.Codigo=@Codigo 
		ORDER BY cs.codigo
	END
END
go


    CREATE PROCEDURE [dbo].[pa_SearchSapCodeFailureComponent]
        @CodigoSapId INT,
        @FallaId INT
        AS
        BEGIN
            SELECT c.Id,c.codigo,c.detalle as nivel FROM Maestro c 
            INNER JOIN TipoMaestro tm ON c.TipoMaestroId = tm.Id
            WHERE tm.Nombre = 'Componentes' AND c.Id IN 
            (SELECT cfc.ComponenteId FROM CodigoSapFallaComponente cfc 
            WHERE  cfc.codigosapFallaId IN (SELECT cf.Id FROM CodigoSapFalla cf
                                            WHERE  cf.CodigoSapId = @CodigoSapId 
                                            AND cf.FallaId = @FallaId))
        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchScrapEntry]
@Novedad varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT i.serial,i.mac,cs.codigo AS codigosap,cs.descripcion FROM Ingreso i 
	INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id 
	INNER JOIN WmsWdGeneral.dbo.estado e ON i.estadoId=e.id
	WHERE e.nombre='SCRAP-TERMINADO' and i.novedad=@Novedad

END




go

CREATE   PROCEDURE [dbo].[pa_SearchSeparatePalletAccesory]
    @Cantidad int,
    @PalletId int,
    @PalletDestinoId int -- Se agrega el ID del pallet destino para la validación
AS
BEGIN
    -- Declarar variables para almacenar el código del pallet origen y si es multimodelo
    DECLARE @CodigoSapOrigen nvarchar(255);
    DECLARE @EsMultimodelo bit;

    -- Obtener el CódigoSap del pallet origen
    SELECT TOP 1 @CodigoSapOrigen = cs.Codigo
    FROM Pallet p
    INNER JOIN CodigoSap cs ON p.CodigoSapId = cs.Id
    WHERE p.Id = @PalletId;

    -- Determinar si el pallet origen es multimodelo
    SET @EsMultimodelo =
    CASE
        WHEN @CodigoSapOrigen LIKE '%MULTIMODELO%' THEN 1
        ELSE 0
    END;

    -- Si el pallet origen es multimodelo
    IF @EsMultimodelo = 1
    BEGIN
        -- Filtrar por el CódigoSap del pallet destino y limitar los resultados
        SELECT TOP(@Cantidad) a.id, cs.Codigo, cs.Descripcion, m.Codigo AS Tipologia
        FROM Accesorio a
        INNER JOIN CodigoSap cs ON a.CodigoSapId = cs.Id
        INNER JOIN Pallet p ON a.PalletId = p.Id
        INNER JOIN Maestro m ON p.TipologiaId = m.Id
        WHERE a.PalletId = @PalletId
          AND cs.Codigo = (SELECT TOP 1 cs_destino.Codigo
                           FROM Pallet p_destino
                           INNER JOIN CodigoSap cs_destino ON p_destino.CodigoSapId = cs_destino.Id
                           WHERE p_destino.Id = @PalletDestinoId);
    END
    ELSE
    BEGIN
        -- Mantener la lógica actual
        SELECT TOP(@Cantidad) a.id, cs.Codigo, cs.Descripcion, m.Codigo AS Tipologia
        FROM Accesorio a
        INNER JOIN CodigoSap cs ON a.CodigoSapId = cs.Id
        INNER JOIN Pallet p ON a.PalletId = p.Id
        INNER JOIN Maestro m ON p.TipologiaId = m.Id
        WHERE a.PalletId = @PalletId;
    END
END;
go


    CREATE PROCEDURE [dbo].[pa_SearchSettings]
        @Pallet varchar(50)
        AS
        BEGIN
			select i.serial,i.codigoSap,i.codigoSapReal,i.pallet,i.palletReal,e.nombre as estado,i.estadoSap,i.estadoRR,i.ajuste 
			from Inventario i inner join WmsWdgeneral.dbo.estado e on i.estadoId=e.id where i.pallet = @Pallet    
        END
go


CREATE PROCEDURE [dbo].[pa_SearchSimpliCodeSap]
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	set nocount on;

    SELECT cs.id,cs.codigo,cs.descripcion FROM CodigoSap cs
    INNER JOIN Maestro t ON cs.tipoId = t.id 
    WHERE t.codigo = 'ACCESORIOS'
    ORDER BY cs.descripcion
END
go

CREATE PROCEDURE [dbo].[pa_SearchSmartCardEntry]
            @EstadoFinal varchar(20)
            AS
            BEGIN

                SET NOCOUNT ON;

                SELECT DISTINCT i.serial,cs.codigo,cs.descripcion,f.codigo AS falla,e.codigo AS estado FROM Ingreso i 
                INNER JOIN SmartCard d ON i.Id=d.serialId 
                INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id 
                INNER JOIN Maestro f ON d.fallaId=f.id 
                INNER JOIN Maestro e ON d.estadoFinalId=e.id 
                INNER JOIN WmsWdGeneral.dbo.estado es ON i.estadoId=es.id 
                WHERE ((es.nombre='SMARTCARD-TERMINADO') and (e.codigo=@EstadoFinal)) and (convert(datetime,d.Fecha) >= convert(datetime,DATEADD(minute,-5,i.FechaMovimiento)))

            END
go


CREATE PROCEDURE [dbo].[pa_SearchStorageGroupPallet]
@Numero varchar(20),
@TipoEquipo VARCHAR(20)
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;
	IF(@Numero='')
	BEGIN
		select p.id,p.numero,count(i.serial)cantidad,cs.codigo as codigoSap,cs.Descripcion as Descripcion,t.codigo as tipologia,po.numero as posicion,o.Nombre as origen,lo.nombre as lote from Pallet p 
		left join Ingreso i on p.id=i.palletId 
		inner join Posicion po on p.posicionId=po.id 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join CodigoSap cs on p.codigoSapId=cs.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
		inner join Estado o on p.OrigenId=o.Id 
		inner join Maestro m on cs.TipoId = m.Id
        LEFT join CodigoSap ci ON i.codigoSapId = ci.id
        LEFT join Maestro mi on ci.TipoId = mi.Id
        left join Lote lo on p.loteId = lo.id
		where e.nombre='ALMACENAMIENTO' and p.activo=1 and m.Codigo = @TipoEquipo --and mi.codigo = @TipoEquipo
		group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.Nombre,lo.nombre,cs.Descripcion
	END
	ELSE
	BEGIN
		select p.id,p.numero,count(i.serial)cantidad,cs.codigo as codigoSap,cs.Descripcion as Descripcion,t.codigo as tipologia,po.numero as posicion,o.Nombre as origen,lo.nombre as lote from Pallet p 
		left join Ingreso i on p.id=i.palletId 
		inner join Posicion po on p.posicionId=po.id 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join CodigoSap cs on p.codigoSapId=cs.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
		inner join Estado o on p.OrigenId=o.Id
		inner join Maestro m on cs.TipoId = m.Id
        LEFT join CodigoSap ci ON i.codigoSapId = ci.id
        LEFT join Maestro mi on ci.TipoId = mi.Id
        left join Lote lo on p.loteId =  lo.id
		where p.Numero=@Numero 
		and e.nombre='ALMACENAMIENTO' and p.activo=1 and m.Codigo = @TipoEquipo --and mi.codigo = @TipoEquipo
		group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.Nombre,lo.nombre,cs.Descripcion
	END

END;
go


CREATE PROCEDURE [dbo].[pa_SearchStorageGroupPalletAccesory]
        @Numero varchar(20),
        @TipoAccesorio varchar (20)        
        AS
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.

            SET NOCOUNT ON;

            IF(@Numero='')
            BEGIN
            select p.id,p.numero,count(i.palletId)cantidad,cs.codigo as codigoSap,cs.descripcion as Descripcion,t.codigo as tipologia,po.numero as posicion,o.Nombre as origen from Pallet p 
            left join Accesorio i on p.id=i.palletId 
            inner join Posicion po on p.posicionId=po.id 
            inner join Maestro t on p.tipologiaId=t.id 
            inner join CodigoSap cs on p.codigoSapId=cs.id 
            inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
            inner join Estado o on p.OrigenId=o.Id 
            inner join Maestro m on cs.TipoId = m.Id
            where e.nombre='ALMACENAMIENTO' and p.activo=1 and m.Codigo = 'ACCESORIOS' and i.TipoAccesorio = @TipoAccesorio
            group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.Nombre,cs.descripcion
        END
        ELSE
        BEGIN
            select p.id,p.numero,count(i.palletId)cantidad,cs.codigo as codigoSap,cs.descripcion as Descripcion,t.codigo as tipologia,po.numero as posicion,o.Nombre as origen from Pallet p 
            left join Accesorio i on p.id=i.palletId 
            inner join Posicion po on p.posicionId=po.id 
            inner join Maestro t on p.tipologiaId=t.id 
            inner join CodigoSap cs on p.codigoSapId=cs.id 
            inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
            inner join Estado o on p.OrigenId=o.Id
            inner join Maestro m on cs.TipoId = m.Id
            where p.Numero=@Numero and e.nombre='ALMACENAMIENTO' and p.activo=1 and m.Codigo = 'ACCESORIOS' and i.TipoAccesorio = @TipoAccesorio
            group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.Nombre,cs.descripcion
        END

    END;
go


CREATE PROCEDURE [dbo].[pa_SearchStorageGroupPalletAccesoryCreated]
        @Numero varchar(20),
        @TipoAccesorio varchar (20)       
        AS
        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.

            SET NOCOUNT ON;

            IF(@Numero='')
            BEGIN
            select p.id,p.numero,count(i.palletId)cantidad,cs.codigo as codigoSap,t.codigo as tipologia,po.numero as posicion,o.Nombre as origen,lo.nombre as lote from Pallet p 
            left join Accesorio i on p.id=i.palletId 
            inner join Posicion po on p.posicionId=po.id 
            inner join Maestro t on p.tipologiaId=t.id 
            inner join CodigoSap cs on p.codigoSapId=cs.id 
            inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
            inner join Estado o on p.OrigenId=o.Id 
            inner join Maestro m on cs.TipoId = m.Id
            left join Lote lo on p.loteId = lo.id
            where e.nombre='ALMACENAMIENTO' and p.activo=1 and m.Codigo = 'ACCESORIOS' and (i.TipoAccesorio = @TipoAccesorio or i.TipoAccesorio is null)
            group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.Nombre,lo.nombre
        END
        ELSE
        BEGIN
            select p.id,p.numero,count(i.palletId)cantidad,cs.codigo as codigoSap,t.codigo as tipologia,po.numero as posicion,o.Nombre as origen,lo.nombre as lote from Pallet p 
            left join Accesorio i on p.id=i.palletId 
            inner join Posicion po on p.posicionId=po.id 
            inner join Maestro t on p.tipologiaId=t.id 
            inner join CodigoSap cs on p.codigoSapId=cs.id 
            inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
            inner join Estado o on p.OrigenId=o.Id
            inner join Maestro m on cs.TipoId = m.Id
            left join Lote lo on p.loteId =  lo.id
            where p.Numero=@Numero and e.nombre='ALMACENAMIENTO' and p.activo=1 and m.Codigo = 'ACCESORIOS' and (i.TipoAccesorio = @TipoAccesorio or i.TipoAccesorio is null)
            group by p.id,p.numero,cs.codigo,t.codigo,po.numero,o.Nombre,lo.nombre
        END

    END
go


CREATE PROCEDURE [dbo].[pa_SearchStoragePallet]
@Numero varchar(20),
@Tipo varchar(20),
@TipoAccesorio varchar(20)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	IF(@Numero='')
	BEGIN
		if(@Tipo='PALLETS')
		begin
			select p.id,p.numero,count(i.serial)cantidad,o.nombre as origen,t.codigo as tipologia,cs.Codigo as codigoSap,cs.descripcion as descripcion,lo.nombre as lote,po.numero as posicion from Pallet p 
			inner join Ingreso i on p.id=i.palletId 
			inner join Posicion po on p.posicionId=po.id 
			inner join Maestro t on p.tipologiaId=t.id 
			inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
			inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id
			inner join CodigoSap cs ON p.CodigoSapId = cs.Id
			inner join Maestro m ON cs.TipoId = m.Id
            left join Lote lo ON p.loteId = lo.id 
			where e.nombre='ALMACENAMIENTO' and p.activo=1 and m.Codigo ='SERIALIZADOS'
			group by p.id,p.numero,o.nombre,t.codigo,lo.nombre,po.numero,cs.Codigo,cs.Descripcion
		end
		else
		begin
			select p.id,p.numero,count(a.Id)cantidad,o.nombre as origen,t.codigo as tipologia,cs.Codigo as codigoSap,cs.descripcion as descripcion,lo.nombre as lote,po.numero as posicion from Pallet p 
			inner join Accesorio a on p.id=a.palletId 
			inner join Posicion po on p.posicionId=po.id 
			inner join Maestro t on p.tipologiaId=t.id 
			inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
			inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
			inner join CodigoSap cs ON p.CodigoSapId = cs.Id
            left join Lote lo ON p.loteId = lo.id
			where e.nombre='ALMACENAMIENTO' and p.activo=1  and a.TipoAccesorio = @TipoAccesorio
			group by p.id,p.numero,o.nombre,t.codigo,lo.nombre,po.numero,cs.Codigo,cs.Descripcion
		end
		
	END
	ELSE
	BEGIN
		if(@Tipo='PALLETS')
		begin
			select p.id,p.numero,count(i.serial)cantidad,o.nombre as origen,t.codigo as tipologia,cs.Codigo as codigoSap,cs.descripcion as descripcion,lo.nombre as lote,po.numero as posicion from Pallet p 
			inner join Ingreso i on p.id=i.palletId 
			inner join Posicion po on p.posicionId=po.id 
			inner join Maestro t on p.tipologiaId=t.id 
			inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
			inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
			inner join CodigoSap cs ON p.CodigoSapId = cs.Id
			inner join Maestro m ON cs.TipoId = m.Id
            left join Lote lo ON p.loteId = lo.id
			where p.Numero=@Numero and e.nombre='ALMACENAMIENTO' and p.activo=1 and m.Codigo ='SERIALIZADOS'
			group by p.id,p.numero,o.nombre,t.codigo,lo.nombre,po.numero,cs.Codigo,cs.Descripcion
		end
		else
		begin
			select p.id,p.numero,count(a.Id)cantidad,o.nombre as origen,t.codigo as tipologia,cs.Codigo as codigoSap,cs.descripcion as descripcion,lo.nombre as lote,po.numero as posicion from Pallet p 
			inner join Accesorio a on p.id=a.palletId 
			inner join Posicion po on p.posicionId=po.id 
			inner join Maestro t on p.tipologiaId=t.id 
			inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
			inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
			inner join CodigoSap cs ON p.CodigoSapId = cs.Id
            left join Lote lo ON p.loteId = lo.id
			where p.Numero=@Numero and e.nombre='ALMACENAMIENTO' and p.activo=1 and a.TipoAccesorio = @TipoAccesorio
			group by p.id,p.numero,o.nombre,t.codigo,lo.nombre,po.numero,cs.Codigo,cs.Descripcion
		end		
	END
END;
go



CREATE PROCEDURE [dbo].[pa_SearchTransitEntry]
@PalletId int
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT i.serial,i.mac,cs.codigo AS codigosap,cs.descripcion,t.codigo AS tipologia,lo.nombre as lote,i.guia,i.documento,i.fecha FROM Ingreso i 
	INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id 
	INNER JOIN Maestro t ON i.tipologiaId=t.id 
    LEFT JOIN Lote lo ON i.loteId = lo.id
    INNER JOIN Maestro tcs ON cs.TipoId = tcs.Id
	WHERE i.palletId=@PalletId and tcs.Codigo = 'SERIALIZADOS'

END
go


CREATE PROCEDURE [dbo].[pa_SearchTransitPallet]
@Numero varchar(20)
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	IF(@Numero='')
	BEGIN
		select p.id,p.numero,count(i.serial)cantidad,o.nombre as origen,t.codigo as tipologia,lo.nombre as lote from Pallet p 
		left join Ingreso i on p.id=i.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
        left join Lote lo ON p.loteId =  lo.id
		where e.nombre='TRANSITO' and p.activo=1 
		group by p.id,p.numero,o.nombre,t.codigo,lo.nombre
	END
	ELSE
	BEGIN
		select p.id,p.numero,count(i.serial)cantidad,o.nombre as origen,t.codigo as tipologia,lo.nombre as lote from Pallet p 
		left join Ingreso i on p.id=i.palletId 
		inner join Maestro t on p.tipologiaId=t.id 
		inner join WmsWdGeneral.dbo.estado o on p.origenId=o.id 
		inner join WmsWdGeneral.dbo.Estado e on p.DestinoId=e.Id 
        left join Lote lo ON p.loteId =  lo.id
		where p.Numero=@Numero and e.nombre='TRANSITO' and p.activo=1 
		group by p.id,p.numero,o.nombre,t.codigo,lo.nombre
	END

END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchUnificationEntry]
@UsuarioId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT i.serial,i.mac,cs.codigo,cs.descripcion,t.codigo AS tipologia FROM Ingreso i 
	INNER JOIN CodigoSap cs ON i.codigoSapId=cs.id 
	INNER JOIN Maestro t ON i.tipologiaId=t.id 
	INNER JOIN WmsWdGeneral.dbo.estado e ON i.estadoId=e.id 
	WHERE e.nombre='UNIFICACION' and i.usuarioIdMovimiento=@UsuarioId

END




go


CREATE PROCEDURE [dbo].[pa_SearchUnreadableEntry]
@PalletId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT CONCAT(i.serial,', ',i.Serial3,', ',i.Serial4,', ',i.Serial5) AS serial,i.mac,il.consecutivoSerial,il.consecutivoMac FROM Ingreso i 
	INNER JOIN Ilegible il ON i.id=il.serialId 
	WHERE i.palletId=@PalletId

END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchUpdatePart]
@Cantidad int,
@EstadoId int,
@Parte varchar(20),
@CodigoSap varchar(20)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	SET NOCOUNT ON;

	select top(@Cantidad) p.id from Parte p 
	inner join CodigoSap cs on p.codigoSapId=cs.id 
	inner join Maestro m on p.parteId=m.id 
	where p.estadoId=@EstadoId and m.codigo=@Parte and cs.codigo=@CodigoSap

END




go


    CREATE PROCEDURE [dbo].[pa_SearchUserClient]
        @ClienteId INT
        AS
        BEGIN
            
            set nocount on;
            SELECT u.Id,u.nombres,u.apellidos,u.nombreUsuario,u.Identificacion
            FROM WmsWdGeneral.dbo.Usuario u 
            INNER JOIN WmsWdGeneral.dbo.UsuarioCliente uc ON u.Id = uc.UsuarioId
            WHERE uc.ClienteId = @ClienteId       
        END
go


CREATE PROCEDURE [dbo].[pa_SearchWorflow]
AS
BEGIN
    SET NOCOUNT ON;

    SELECT w.id, m.nombre AS modulo, o.nombre AS origen, ISNULL(p.nombre, 'NINGUNA') AS opcion, p.descripcion, ISNULL(t.codigo, 'NINGUNA') AS tipologia, ISNULL(n.codigo, 'NINGUNA') AS nivel 
    FROM WorkFlow w 
    INNER JOIN WmsWdGeneral.dbo.Modulo m ON w.moduloId = m.id 
    INNER JOIN WmsWdGeneral.dbo.estado o ON w.origenId = o.id 
    LEFT JOIN WmsWdGeneral.dbo.permiso p ON w.opcionId = p.id 
    LEFT JOIN Maestro t ON w.tipologiaId = t.id
    LEFT JOIN Maestro n ON w.nivelId = n.Id;
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SearchWorflowDestiny]
@WorkflowId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	set nocount on;

	select e.id,e.nombre from WorkFlowDestino w 
	inner join WmsWdGeneral.dbo.estado e on w.destinoId=e.id 
	where w.workFlowId=@WorkflowId 
	order by e.nombre

END




go


-- =============================================
-- Author:		MEDINA DAVID
-- ALTER date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SendAccesory]
@PalletId int,
@EstadoId int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	UPDATE Accesorio set EstadoId=@EstadoId where PalletId=@PalletId
	select @Filas=@@ROWCOUNT
END







go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SendAllPallet]
@DestinoId int,
@PalletId int
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    update Pallet set destinoId=@DestinoId where id=@PalletId

END




go


CREATE PROCEDURE [dbo].[pa_SendCosmeticEntry]
@EstadoId int,
@TipologiaId int,
@UsuarioId int,
@PalletId int,
@Filas int output
AS
BEGIN
	UPDATE Ingreso SET estadoId=@EstadoId,tipologiaId=@TipologiaId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
	WHERE palletId=@PalletId
	select @Filas=@@ROWCOUNT
END
go

CREATE   PROCEDURE [dbo].[pa_SendEntry]
	@EstadoId int,
	@TipologiaId int,
	@UsuarioId int,
	@PalletId int,
	@Opcion int,
	@Filas int output
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @BatchSize INT = 500;
    DECLARE @ProcessedRows INT = 0;
    DECLARE @CurrentBatch INT;
    DECLARE @EsMultimodelo BIT;
    DECLARE @PalletTipologiaId INT;
    DECLARE @TipologiaFinal INT;

    -- Obtener información del pallet
    SELECT 
        @EsMultimodelo = CASE WHEN cs.Codigo = 'MULTIMODELO' THEN 1 ELSE 0 END,
        @PalletTipologiaId = p.TipologiaId
    FROM Pallet p
    INNER JOIN CodigoSap cs ON p.CodigoSapId = cs.id
    WHERE p.Id = @PalletId;

    -- Determinar qué tipología usar
    -- Si es MULTIMODELO y el pallet tiene tipología SCRAP, usar la del pallet
    IF @EsMultimodelo = 1 AND EXISTS (
        SELECT 1 
        FROM Maestro m 
        WHERE m.Id = @PalletTipologiaId 
        AND m.Codigo = 'SCRAP'
    )
    BEGIN
        SET @TipologiaFinal = @PalletTipologiaId;
    END
    ELSE
    BEGIN
        SET @TipologiaFinal = @TipologiaId;
    END

    SET @Filas = 0;

    -- Procesar en batches SEPARADOS (commit por batch)
    WHILE 1 = 1
    BEGIN
        BEGIN TRANSACTION;

        BEGIN TRY
            UPDATE TOP (@BatchSize) Ingreso WITH (ROWLOCK, READPAST)
            SET
                estadoId = @EstadoId,
                tipologiaId = CASE 
                    WHEN @EsMultimodelo = 0 THEN @TipologiaFinal
                    ELSE @TipologiaFinal -- Ahora siempre asigna tipología, incluso para MULTIMODELO con SCRAP
                END,
                usuarioIdMovimiento = @UsuarioId,
                fechaMovimiento = GETDATE(),
                palletId = CASE WHEN @Opcion = 1 THEN 0 ELSE palletId END
            WHERE
                PalletId = @PalletId
                AND estadoId <> @EstadoId;

            SET @CurrentBatch = @@ROWCOUNT;
            SET @Filas = @Filas + @CurrentBatch;

            COMMIT TRANSACTION;

            -- Si no hay más registros, salir
            IF @CurrentBatch = 0 OR @CurrentBatch < @BatchSize
                BREAK;

            -- Pequeña pausa para no saturar (opcional, reducido)
            WAITFOR DELAY '00:00:00.010';

        END TRY
        BEGIN CATCH
            IF @@TRANCOUNT > 0
                ROLLBACK TRANSACTION;

            RAISERROR('Error procesando batch', 16, 1);
            RETURN -1;
        END CATCH
    END

    RETURN 0;
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SendModelsEntry]
@EstadoId int,
@UsuarioId int,
@PalletIdDestino int,
@PalletId int,
@CodigoSapId int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	UPDATE Ingreso SET estadoId=@EstadoId,UsuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE(),PalletId=@PalletIdDestino 
	WHERE palletId=@PalletId and codigoSapId=@CodigoSapId
	select @Filas=@@ROWCOUNT
END




go


CREATE PROCEDURE [dbo].[pa_SendNoveltyEntry]
	@EstadoId int,
	@TipologiaId int,
	@UsuarioId int,
	@PalletId int,
	@Opcion int,
	@Filas int output
AS

BEGIN

    DECLARE @BatchSize INT = 200;
    DECLARE @TotalRows INT = (SELECT COUNT(*) FROM Ingreso  WHERE PalletId = @PalletId AND estadoId <> @EstadoId );
    DECLARE @ProcessedRows INT = 0;

    IF((SELECT cs.Codigo FROM Pallet p
        INNER JOIN CodigoSap cs ON p.CodigoSapId = cs.id
        WHERE p.Id = @PalletId)<>'MULTIMODELO')

        BEGIN
            IF(@Opcion=0)

                BEGIN
                    
                    BEGIN TRANSACTION;
                    WHILE @ProcessedRows < @TotalRows
                    BEGIN
                        UPDATE TOP (@BatchSize) Ingreso
                        SET estadoId = @EstadoId,
                            tipologiaId=@TipologiaId,
                            usuarioIdMovimiento = @UsuarioId,
                            fechaMovimiento = GETDATE(),
                            novedad = '0'
                            WHERE PalletId = @PalletId AND estadoId <> @EstadoId;
                        SET @ProcessedRows = @ProcessedRows + @@ROWCOUNT;
                        SELECT @Filas=@@ROWCOUNT
                        WAITFOR DELAY '00:00:00.100';
                    END
                    COMMIT TRANSACTION;
                    
                END
            ELSE
                BEGIN
                   BEGIN TRANSACTION;
                    WHILE @ProcessedRows < @TotalRows
                    BEGIN
                        UPDATE TOP (@BatchSize) Ingreso
                        SET estadoId = @EstadoId,
                            tipologiaId=@TipologiaId,
                            usuarioIdMovimiento = @UsuarioId,
                            fechaMovimiento = GETDATE(),
                            palletId = 0,
                            novedad = '0'
                            WHERE PalletId = @PalletId AND estadoId <> @EstadoId;
                        SET @ProcessedRows = @ProcessedRows + @@ROWCOUNT;
                        SELECT @Filas=@@ROWCOUNT
                        WAITFOR DELAY '00:00:00.100';
                    END
                    COMMIT TRANSACTION;
                END	
        END
    ELSE
        BEGIN
            IF(@Opcion=0)
                BEGIN
                   BEGIN TRANSACTION;
                    WHILE @ProcessedRows < @TotalRows
                    BEGIN
                        UPDATE TOP (@BatchSize) Ingreso
                        SET estadoId = @EstadoId,
                            usuarioIdMovimiento = @UsuarioId,
                            fechaMovimiento = GETDATE(),
                            novedad = '0'
                            WHERE PalletId = @PalletId AND estadoId <> @EstadoId;
                        SET @ProcessedRows = @ProcessedRows + @@ROWCOUNT;
                        SELECT @Filas=@@ROWCOUNT
                        WAITFOR DELAY '00:00:00.100';
                    END
                    COMMIT TRANSACTION;
                END
            ELSE
                BEGIN
                    BEGIN TRANSACTION;
                    WHILE @ProcessedRows < @TotalRows
                    BEGIN
                        UPDATE TOP (@BatchSize) Ingreso
                        SET estadoId = @EstadoId,
                            usuarioIdMovimiento = @UsuarioId,
                            fechaMovimiento = GETDATE(),
                            palletId=0,
                            novedad = '0'
                            WHERE PalletId = @PalletId AND estadoId <> @EstadoId;
                        SET @ProcessedRows = @ProcessedRows + @@ROWCOUNT;
                        SELECT @Filas=@@ROWCOUNT
                        WAITFOR DELAY '00:00:00.100';
                    END
                    COMMIT TRANSACTION;
                END	
        END
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SendPallet]
@DestinoId int,
@TipologiaId int,
@PosicionId int,
@Estado int,
@PalletId int,
@Filas int output
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    update Pallet set destinoId=@DestinoId,tipologiaId=@TipologiaId,posicionId=@PosicionId,activo=@Estado 
	where id=@PalletId
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_SendPart]
@Opcion int,
@EstadoId int,
@UsuarioId int,
@PalletId int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	IF(@Opcion=0)
	BEGIN
		update Parte set estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
		where palletId=@PalletId
	END
	ELSE
	BEGIN
		update Parte set estadoId=@EstadoId,palletId=0,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
		where palletId=@PalletId
	END
	select @Filas=@@ROWCOUNT
END




go

CREATE PROCEDURE [dbo].[pa_SendStorageEntry]
@EstadoId int,
@TipologiaId int,
@UsuarioId int,
@PalletId int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	UPDATE Ingreso SET estadoId=@EstadoId,tipologiaId=@TipologiaId,palletIdAlmacen=@PalletId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
	WHERE palletId=@PalletId
	select @Filas=@@ROWCOUNT
END
go


CREATE PROCEDURE [dbo].[pa_SerialesByPallet]
	@palletId int
	AS
	BEGIN
		SELECT i.serial FROM Ingreso i
        INNER JOIN CodigoSap cs ON i.CodigoSapId =  cs. Id
        INNER JOIN Maestro t ON cs.TipoId = t.Id
		WHERE i.palletId = @palletId AND t.Codigo = 'SERIALIZADOs'
	END
go

CREATE PROCEDURE [dbo].[pa_ToggleForecastActivo] @id INT, @estado BIT, @Filas INT OUTPUT AS BEGIN SET NOCOUNT ON; UPDATE Forecast SET activo = @estado, updated_at = GETDATE() WHERE id = @id; SET @Filas = @@ROWCOUNT; END;
go

CREATE PROCEDURE [dbo].[pa_ToggleManoObraActivo] @Id INT, @estado BIT, @Filas INT OUTPUT AS BEGIN SET NOCOUNT ON; UPDATE ManoDeObra SET activo = @estado, updated_at = GETDATE() WHERE id = @Id; SET @Filas = @@ROWCOUNT; END;
go


        CREATE PROCEDURE [dbo].[pa_UnifyAccesory]
        @PalletIdDestino int,
        @PalletId int
        AS
        /*
        DECLARE @PalletId INT
        SET @PalletId=0
        EXEC pa_UpdateDiagnostic @PalletId
        */
        BEGIN
            UPDATE Accesorio SET palletId=@PalletIdDestino,fecha_movimiento = GETDATE()
            WHERE palletId=@PalletId
        END
go


-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UnifyEntry]
@PalletIdDestino int,
@TipologiaId int,
@UsuarioId int,
@PalletId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	UPDATE Ingreso SET palletId=@PalletIdDestino,PalletIdAlmacen=@PalletIdDestino,tipologiaId=@TipologiaId,UsuarioIdMovimiento=@UsuarioId 
	WHERE palletId=@PalletId
END





go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UnifyPallet]
@PalletId int
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    update Pallet set Activo=0 
	where id=@PalletId
END




go


        CREATE   PROCEDURE [dbo].[pa_UpdateAccesory]
            @Cantidad INT,
            @DestinoId INT,
            @EstadoLimpiezaId INT,
            @Codigo VARCHAR(30),
            @Estado VARCHAR(30),
            @TipoAccesorio VARCHAR(20),
            @UsuarioLimpiezaId INT,
            @Filas INT OUTPUT
        AS
        BEGIN
            SET NOCOUNT ON;

            DECLARE @EstadoActualId INT;
            SELECT @EstadoActualId = ID FROM ESTADO WHERE Nombre = @Estado;

            IF (@Estado = 'ELIMINAR')
            BEGIN
                UPDATE Accesorio
                SET EstadoId = @DestinoId,
                    EstadoLimpiezaId = 0,
                    UsuarioLimpiezaId = 0
                WHERE id IN (
                    SELECT TOP(@Cantidad) a.id
                    FROM Accesorio a
                    INNER JOIN CodigoSap cs ON a.CodigoSapId = cs.Id
                    WHERE a.EstadoId = (SELECT ID FROM ESTADO WHERE Nombre = 'LIMPIEZA-ASIGNADO')
                      AND a.TipoAccesorio = @TipoAccesorio
                      AND a.UsuarioLimpiezaId = @UsuarioLimpiezaId
                      AND a.EstadoLimpiezaId = @EstadoLimpiezaId
                );
                SELECT @Filas = @@ROWCOUNT;
            END
            ELSE IF (@DestinoId = 96)
            BEGIN
                UPDATE Accesorio
                SET EstadoId = @DestinoId,
                    EstadoLimpiezaId = @EstadoLimpiezaId,
                    UsuarioLimpiezaId = @UsuarioLimpiezaId
                WHERE id IN (
                    SELECT TOP(@Cantidad) a.id
                    FROM Accesorio a
                    INNER JOIN CodigoSap cs ON a.CodigoSapId = cs.Id
                    WHERE a.EstadoId = @EstadoActualId
                      AND a.TipoAccesorio = @TipoAccesorio
                );
                SELECT @Filas = @@ROWCOUNT;
            END
            ELSE
            BEGIN
                UPDATE Accesorio
                SET EstadoId = @DestinoId,
                    EstadoLimpiezaId = @EstadoLimpiezaId,
                    UsuarioLimpiezaId = @UsuarioLimpiezaId
                WHERE id IN (
                    SELECT TOP(@Cantidad) a.id
                    FROM Accesorio a
                    INNER JOIN CodigoSap cs ON a.CodigoSapId = cs.Id
                    WHERE cs.Codigo = @Codigo
                      AND a.EstadoId = @EstadoActualId
                      AND a.TipoAccesorio = @TipoAccesorio
                );
                SELECT @Filas = @@ROWCOUNT;
            END
        END
go


-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateAllRepair]
@EstadoFinalId int,
@Falla1Id int,
@Falla2Id int,
@Falla3Id int,
@Falla4Id int,
@PartesCambiadas varchar(150),
@MotivoScrapId int,
@TecnicoReparacionId int,
@Serial varchar(50)
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	update Reparacion set estadoFinalId=@EstadoFinalId,falla1Id=@Falla1Id,falla2Id=@Falla2Id,falla3Id=@Falla3Id,falla4Id=@Falla4Id,partesCambiadas=@PartesCambiadas,MotivoScrapId=@MotivoScrapId,tecnicoReparacionId=@TecnicoReparacionId,fechaReparacion=GETDATE() 
	where id=(select max(r.id)id from Reparacion r 
	inner join Maestro m on r.estadoFinalId=m.id 
	where r.serial=@Serial and m.codigo='ASIGNADO')
END




go


CREATE   PROCEDURE [dbo].[pa_UpdateAssemble]
    @UsuarioIdAsignado INT,
    @Serial VARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	UPDATE Ensamble SET UsuarioIdAsignado=@UsuarioIdAsignado
	WHERE id=(SELECT max(e.id) 
                FROM Ensamble e
				INNER JOIN Ingreso i ON e.serialId=i.id 
                WHERE i.Serial = @Serial)
END
go

CREATE PROCEDURE [dbo].[pa_UpdateBatchPalletEntry]
        @LoteId int,
        @UsuarioId int,
        @PalletId INT,
        @Filas int output
        AS
        /*
        DECLARE @PalletId INT
        SET @PalletId=0
        EXEC pa_UpdateDiagnostic @PalletId
        */
        BEGIN
            IF((SELECT cs.Codigo from CodigoSap cs INNER JOIN Pallet p ON cs.id = p.CodigoSapId
                WHERE p.Id= @PalletId) = 'MULTIMODELO' )
            BEGIN
                UPDATE Pallet SET loteId=@LoteId,FechaModifica=GETDATE(),UsuarioIdModifica=@UsuarioId 
                WHERE Id=@PalletId
                select @Filas=@@ROWCOUNT
            END
            ELSE
            BEGIN
                UPDATE Pallet SET loteId=@LoteId,FechaModifica=GETDATE(),UsuarioIdModifica=@UsuarioId 
                WHERE Id=@PalletId
                UPDATE Ingreso SET loteId=@LoteId,fechaMovimiento=GETDATE(),usuarioIdMovimiento=@UsuarioId 
                WHERE palletId=@PalletId
                select @Filas=@@ROWCOUNT
            END
        END
go

CREATE PROCEDURE [dbo].[pa_UpdateBatchSerialEntry]
        @LoteId int,
        @UsuarioId int,
        @Serial varchar(50),
        @Filas int output
        AS
        /*
        DECLARE @PalletId INT
        SET @PalletId=0
        EXEC pa_UpdateDiagnostic @PalletId
        */
        BEGIN
            UPDATE Ingreso SET loteId=@LoteId,fechaMovimiento=GETDATE(),usuarioIdMovimiento=@UsuarioId 
            WHERE serial=@Serial
            select @Filas=@@ROWCOUNT
        END
go

CREATE PROCEDURE [dbo].[pa_UpdateByPallet]
	@PalletId int,
	@DestinyId int,
	@PositionId int,
	@UserId int
AS
/*
DECLARE @PalletId int
SET @PalletId = 1
DECLARE @DestinyId int
SET @DestinyId = 1
DECLARE @PositionId int
SET @PositionId = 1
DECLARE @UserId int
SET @UserId = 1
EXEC pa_UpdateByPallet @PalletId,@DestinyId,@PositionId,@UserId
*/
BEGIN
	UPDATE dbo.Pallet SET DestinoId = @DestinyId, PosicionId = @PositionId, UsuarioIdModifica = @UserId, FechaModifica = GETDATE() WHERE Id =  @PalletId
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateChangedEntry]
@Serial1 varchar(50),
@Serial2 varchar(50),
@Mac varchar(50),
@EstadoId int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ingreso SET serial=@Serial2,mac=@Mac,estadoId=@EstadoId 
	WHERE id=(SELECT id FROM Ingreso 
	WHERE serial=@Serial1)
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateChangedPackingEntry]
@EstadoId int,
@PalletId int,
@CajaEmpaqueId int,
@Serial varchar(50),
@UsuarioId int,
@tipologiaId int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,palletIdEmpaque=@PalletId,cajaEmpaqueId=@CajaEmpaqueId,UsuarioIdMovimiento=@UsuarioId,TipologiaId=@tipologiaId 
	WHERE serial=@Serial
	select @Filas=@@ROWCOUNT
END



go

CREATE PROCEDURE [dbo].[pa_UpdateCiudad]
        @Nombre varchar(50),
        @CodigoCiudad varchar(120),
        @Id int,
        @Filas int output
        AS

        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.

            UPDATE Ciudad SET nombre=@Nombre,codigoCiudad=@CodigoCiudad 
            WHERE id=@Id
            select @Filas=@@ROWCOUNT
        END
go

CREATE   PROCEDURE [dbo].[pa_UpdateClasificacion]
@EstadoEnviado VARCHAR(30),
@NivelId INT,
@Serial VARCHAR(50)
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.	
	UPDATE Clasificacion
	SET EstadoEnviado=@EstadoEnviado,
		nivelId=@NivelId,
		Fecha=GETDATE()
	WHERE id=
		(SELECT max(id)id
		 FROM Clasificacion
		 WHERE serial=@Serial)
END
go

CREATE PROCEDURE [dbo].[pa_UpdateClasificationEntry]
    @EstadoId INT,
    @NivelId INT,
    @UsuarioId INT,
    @Serial VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE i
    SET estadoId = @EstadoId,
        nivelId = @NivelId,
        usuarioIdMovimiento = @UsuarioId,
        fechaMovimiento = SYSDATETIME()
    FROM Ingreso i WITH (ROWLOCK)
    WHERE i.Serial = @Serial;

END
go


CREATE   PROCEDURE [dbo].[pa_UpdateCleaning]
    @Serial VARCHAR(50)
AS
BEGIN
    UPDATE Limpieza 
    SET Asignacion_Confirmada = '1'            
    WHERE Id = (
        SELECT MAX(l.Id)
        FROM Limpieza l
        INNER JOIN Ingreso i ON l.SerialId = i.Id
        WHERE i.Serial = @Serial
    );
END;
go


-- =============================================
-- Author:		MEDINA DAVID
-- ALTER date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateDeliveryAccesory]
@Id int,
@DestinoId int,
@PalletId int
AS
/*
DECLARE @Id int=26484
DECLARE @DestinoId int=93
DECLARE @PalletId int=0
EXEC pa_UpdateDeliveryAccesory @Id,@DestinoId,@PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	update Accesorio set EstadoId=@DestinoId,PalletId=@PalletId where id=@Id
END







go


CREATE PROCEDURE [dbo].[pa_UpdateDiagnostic]
@EstadoFinalId INT,
@FallaId INT,
@Serial VARCHAR(50),
@EstadoCalidadId INT
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.	
	UPDATE Diagnostico
	SET estadoFinalId=@EstadoFinalId,
		fallaId=@FallaId,
		Fecha=GETDATE(),
		EstadoCalidadId=@EstadoCalidadId
	WHERE id=
		(SELECT max(id)id
		 FROM Diagnostico
		 WHERE serial=@Serial)
END;
go


CREATE PROCEDURE [dbo].[pa_UpdateDispatchEntry]
@EstadoId int,
@PalletId int,
@CajaDespachoId int,
@UsuarioId int,
@Serial varchar(50),
@LoteId INT,
@Filas int output
AS

BEGIN
	UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,cajaDespachoId=@CajaDespachoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE(),loteId = @LoteId 
	WHERE serial=@Serial
	select @Filas=@@ROWCOUNT
END
go


    CREATE PROCEDURE [dbo].[pa_UpdateEntryNotAvailable]
		@Serial VARCHAR(50),
        @Mac VARCHAR(50),
        @ModeloId INT,
        @CodigoSapId INT,
        @TipologiaId INT,
        @LoteId INT,
        @TipoOrigenId INT,
        @OrigenId INT,
        @Fecha DATETIME,
        @UsuarioId INT,
        @Filas INT OUT
		AS
		BEGIN
			-- SET NOCOUNT ON added to prevent extra result sets from
			-- interfering with SELECT statements.
			
			set nocount on;
			UPDATE Ingreso SET serial = UPPER(@Serial),Mac=UPPER(@Mac) ,modeloId =@ModeloId,codigoSapId = @CodigoSapId,tipologiaId = @TipologiaId,loteId =@LoteId,tipoOrigenId = @TipoOrigenId,origenId=@OrigenId,fecha=@Fecha,usuarioIdMovimiento=@UsuarioId
            WHERE Serial=@Serial
            SELECT @Filas = @@ROWCOUNT

		END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateFinalStateQuality]
@PalletId INT,
@EstadoFinalId INT,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateFinalStateQuality @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	UPDATE Calidad SET estadoFinalId=@EstadoFinalId	
	WHERE palletId=@PalletId and estadoFinalId=0
	select @Filas=@@ROWCOUNT
END




go

CREATE PROCEDURE [dbo].[pa_UpdateForecast] @id INT, @linea_negocio NVARCHAR(255) = NULL, @fecha DATE, @fecha_entrega DATE = NULL, @forecast_und DECIMAL(18,2) = 0, @jornada NVARCHAR(8) = 'LV', @dias_habiles INT = NULL, @observaciones NVARCHAR(500) = NULL, @usuario NVARCHAR(150) = NULL, @familias_json NVARCHAR(MAX) = NULL, @Filas INT OUTPUT AS BEGIN SET NOCOUNT ON; UPDATE Forecast SET linea_negocio = @linea_negocio, fecha = @fecha, fecha_entrega = @fecha_entrega, forecast_und = @forecast_und, jornada = @jornada, dias_habiles = @dias_habiles, observaciones = @observaciones, usuario = @usuario, familias_json = @familias_json, updated_at = GETDATE() WHERE id = @id AND activo = 1; SET @Filas = @@ROWCOUNT; END;
go

CREATE PROCEDURE [dbo].[pa_UpdateIncomeByPallet]
	@PalletId int,
	@DestinyId int,
	@Option int,
	@UserId int
AS
/*
DECLARE @PalletId int
SET @PalletId = 16
DECLARE @DestinyId int
SET @DestinyId = 1
DECLARE @Option int
SET @Option = 1
DECLARE @UserId int
SET @UserId = 1
EXEC pa_UpdateIncomeByPallet @PalletId,@DestinyId,@Option,@UserId
*/
BEGIN
	IF(@Option=0)
	BEGIN
       UPDATE dbo.Ingreso SET EstadoId = @DestinyId, UsuarioIdMovimiento = @UserId, FechaMovimiento = GETDATE() WHERE PalletId = @PalletId
    END
	ELSE
	BEGIN
	   UPDATE dbo.Ingreso SET EstadoId = @DestinyId, PalletId = 0, CajaEmpaqueId = 0, UsuarioIdMovimiento = @UserId, FechaMovimiento = GETDATE() WHERE PalletId = @PalletId
    END
END
go

CREATE PROCEDURE [dbo].[pa_UpdateLabel]
@Nombre VARCHAR(30),
@Tipo VARCHAR(20),
@Impresion INT,
@CodigoSapId INT,
@Id INT,
@Filas int output
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Etiqueta SET nombre=@Nombre,tipo=@Tipo,impresion=@Impresion,codigoSapId=@CodigoSapId 
	WHERE id=@Id
	select @Filas=@@ROWCOUNT
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateLabelField]
@EtiquetaId int,
@Nombre varchar(20),
@Valor varchar(20),
@Id int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	update EtiquetaCampo set EtiquetaId=@EtiquetaId,Nombre=@Nombre,Valor=@Valor 
	where id=@Id
	select @Filas=@@ROWCOUNT
END



go


    CREATE PROCEDURE [dbo].[pa_UpdateLevel]
    @NivelId int,
    @PalletId int,
    @Filas int OUTPUT
    AS
    BEGIN
        UPDATE i 
        SET i.nivelId = @NivelId 
        FROM Ingreso i
        JOIN Maestro nuevoNivel ON nuevoNivel.Id = @NivelId
        JOIN Maestro actualNivel ON actualNivel.Id = i.nivelId
        WHERE i.palletId = @PalletId 
        AND CONVERT(int, nuevoNivel.Codigo) > CONVERT(int, actualNivel.Codigo);
        
        SELECT @Filas = @@ROWCOUNT;
    END
go

CREATE PROCEDURE [dbo].[pa_UpdateLote]
        @Nombre varchar(50),
        @Descripcion varchar(120),
        @Id int,
        @Filas int output
        AS

        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.

            UPDATE Lote SET nombre=@Nombre,descripcion=@Descripcion 
            WHERE id=@Id
            select @Filas=@@ROWCOUNT
        END
go

CREATE PROCEDURE [dbo].[pa_UpdateManoDeObra] @Fecha DATE, @Detalle VARCHAR(50) = NULL, @Observaciones VARCHAR(50) = NULL, @mano_obra_json NVARCHAR(MAX) = NULL, @usuario NVARCHAR(150) = NULL, @Id INT, @Filas INT OUTPUT AS BEGIN UPDATE ManoDeObra SET fecha = @Fecha, detalle = @Detalle, observaciones = @Observaciones, mano_obra_json = ISNULL(@mano_obra_json, mano_obra_json), usuario = ISNULL(@usuario, usuario), updated_at = GETDATE() WHERE id = @Id; SELECT @Filas = @@ROWCOUNT; END;
go


CREATE PROCEDURE [dbo].[pa_UpdateMaster]
@Codigo varchar(50),
@Descripcion varchar(50),
@Detalle varchar(150),
@Adicional varchar(50),
@TipoMaestroId int,
@Id int,
@Filas int output
AS

BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Maestro SET codigo=@Codigo,descripcion=@Descripcion,Detalle=@Detalle,adicional=@Adicional,tipoMaestroId=@TipoMaestroId 
	WHERE id=@Id
	select @Filas=@@ROWCOUNT
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateMasterType]
@Nombre varchar(50),
@Descripcion varchar(50),
@TipoMaestroId int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	update TipoMaestro set nombre=@Nombre,descripcion=@Descripcion where id=@TipoMaestroId
	select @Filas=@@ROWCOUNT
END




go


CREATE   PROCEDURE [dbo].[pa_UpdateNoveltyAllEntry]
@Serial varchar(50),
@EstadoId int,
@TipologiaId int,
@Observaciones varchar(100),
@Novedad varchar(50),
@UsuarioId int,
@FallaCosmeticaId int,
@FallaFuncionalId int
AS
BEGIN
	UPDATE Ingreso
	SET observaciones=@Observaciones,
		novedad=@Novedad,
		estadoId=@EstadoId,
		tipologiaId=@TipologiaId,
		usuarioIdMovimiento=@UsuarioId,
		fechaMovimiento=GETDATE(),
		FallaCosmeticaId=@FallaCosmeticaId,
		FallaFuncionalId=@FallaFuncionalId
	WHERE serial=@Serial
END
go

/**
 * Author: Pedro Neira
 * Created: FEB, 18  2025
 */ 

 CREATE PROCEDURE [dbo].[pa_UpdateNoveltyEntry]
@Id int,
@Serial varchar(50),
@Mac varchar(50),
@CodigoSapId int,
@Guia varchar(50),
@Documento varchar(50),
@TipoOrigenId int,
@OrigenId int,
@TipologiaId int,
@EstadoId int,
@TipoNovedad varchar(30),
@UsuarioId int,
@Filas int output
AS
BEGIN
	IF(@TipoNovedad='ASOCIADO' OR @TipoNovedad='NO CARGADO SAP' OR @TipoNovedad='NO EXISTE PREALERTA' OR @TipoNovedad='CONTRATADO' OR @TipoNovedad='REINGRESOS COMPLETADO' OR @TipoNovedad='NO CONFORMIDAD' OR @TipoNovedad='OTRO ALMACEN SAP')
	BEGIN
		UPDATE Ingreso SET observaciones='Verificado sistemas cliente',tipologiaId=@TipologiaId,estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
		WHERE id=@Id
	END
	ELSE IF(@TipoNovedad='ERROR CODIGO SAP')
	BEGIN
		UPDATE Ingreso SET codigoSapId=@CodigoSapId,observaciones='Cambio codigo Sap',tipologiaId=@TipologiaId,estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
		WHERE id=@Id
	END
	ELSE IF(@TipoNovedad='ERROR SERIAL-MAC')
	BEGIN
		UPDATE Ingreso SET serial=UPPER(@Serial),mac=UPPER(@Mac),observaciones='Cambio serial-mac',tipologiaId=@TipologiaId,estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
		WHERE id=@Id
	END
    ELSE IF(@TipoNovedad='ERROR GUIA-DOCUMENTO')
    BEGIN
        UPDATE Ingreso SET guia=@Guia,Documento=@Documento,observaciones='Cambio guia-documento',tipologiaId=@TipologiaId,estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
		WHERE id=@Id
    END
    ELSE IF(@TipoNovedad='ERROR ORIGEN')
    BEGIN
        UPDATE Ingreso SET TipoOrigenId=@TipoOrigenId,OrigenId=@OrigenId,observaciones='Cambio de origen',tipologiaId=@TipologiaId,estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
		WHERE id=@Id
    END 
    ELSE
    BEGIN 
        UPDATE Ingreso SET observaciones='Verificado sistemas cliente',tipologiaId=@TipologiaId,estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
		WHERE id=@Id
    END
	select @Filas=@@ROWCOUNT
END
go

CREATE PROCEDURE [dbo].[pa_UpdateObservationPqrs]
        @SerialId INT,
        @Serial VARCHAR(50),
        @Observacion NVARCHAR(MAX),
        @Filas int output
        AS

        BEGIN

            UPDATE PQRS SET x_studio_observaciones=@Observacion 
            WHERE name = @Serial and SerialId = @SerialId
            select @Filas=@@ROWCOUNT
        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdatePacking]
@SerialId INT,
@SerialNuevo VARCHAR(50),
@Mac VARCHAR(50),
@SerialAnterior VARCHAR(50),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Empaque SET serialId=@SerialId,serial=@SerialNuevo,mac=@Mac 
	WHERE id =(SELECT max(id) FROM Empaque 
	WHERE serial=@SerialAnterior)
	select @Filas=@@ROWCOUNT
END




go


CREATE PROCEDURE [dbo].[pa_UpdatePackingAccesory]
@CodigoSap varchar(20),
@Tipo varchar(20),
@Estado varchar(20),
@DestinoId int,
@Serial varchar(50),
@PalletId int,
@Cantidad int 
AS
BEGIN
    if(@Estado='EMPAQUE') BEGIN
	UPDATE Accesorio
	SET EstadoId=@DestinoId,
		SerialEmpaque=@Serial,
		previousPalletId = palletId,
		palletId= @PalletId
	WHERE id IN
		(SELECT top(@Cantidad) a.id
		 FROM Accesorio a
		 INNER JOIN CodigoSap cs ON a.CodigoSapId=cs.Id
		 INNER JOIN WmsWdGeneral.dbo.Estado e ON a.EstadoId=e.Id
		 WHERE cs.Codigo=@CodigoSap
		   AND a.TipoAccesorio=@Tipo
		   AND e.Nombre=@Estado) END
	ELSE BEGIN
	UPDATE Accesorio
	SET EstadoId=@DestinoId,
		SerialEmpaque='0',
		palletId = previousPalletId
	WHERE id IN
		(SELECT a.id
		 FROM Accesorio a
		 INNER JOIN CodigoSap cs ON a.CodigoSapId=cs.Id
		 INNER JOIN WmsWdGeneral.dbo.Estado e ON a.EstadoId=e.Id
		 WHERE cs.Codigo=@CodigoSap
		   AND e.Nombre=@Estado
		   AND a.PalletId=@PalletId
	) END
END
go


CREATE PROCEDURE [dbo].[pa_UpdatePackingAllEntry]
@EstadoId int,
@UsuarioId int,
@Serial varchar(50)
AS

BEGIN
	UPDATE Ingreso SET estadoId=@EstadoId,palletId=0,PalletIdEmpaque=0,cajaEmpaqueId=0,cajaDespachoId=0,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE(),smartCardId =  NULL,SmartCard =  NULL 
	WHERE serial=@Serial
END
go


CREATE PROCEDURE [dbo].[pa_UpdatePackingEntry]
@EstadoId int,
@PalletId int,
@CajaEmpaqueId int,
@UsuarioId int,
@Serial varchar(50),
@LoteId INT,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,palletIdEmpaque=@PalletId,cajaEmpaqueId=@CajaEmpaqueId,CajaDespachoId=0,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE(),loteId = @LoteId 
	WHERE serial=@Serial
	select @Filas=@@ROWCOUNT
END

go

CREATE PROCEDURE [dbo].[pa_UpdatePackingEntrySmartCard]
        @EstadoId int,
        @PalletId int,
        @CajaEmpaqueId int,
        @UsuarioId int,
        @Serial varchar(50),
        @LoteId INT,
        @SmartCardId INT,
        @SmartCard VARCHAR(50),
        @Filas int output
        AS

        BEGIN
            UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,palletIdEmpaque=@PalletId,cajaEmpaqueId=@CajaEmpaqueId,CajaDespachoId=0,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE(),loteId = @LoteId,smartCardId = @SmartCardId,SmartCard = @SmartCard 
            WHERE serial=@Serial
            select @Filas=@@ROWCOUNT
        END
go


-- =============================================
-- Author:		MEDINA DAVID
-- ALTER date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdatePackingOffAccesory]
@Serial varchar(50),
@DestinoId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	update Accesorio set EstadoId=@DestinoId,SerialEmpaque=0 where serialEmpaque=@Serial
END







go

CREATE PROCEDURE [dbo].[pa_UpdatePackingSmartCard]
            @SmartCardId INT,
            @SmartCard VARCHAR(50),
            @Serial VARCHAR(50),
            @Filas int output
            AS
            
            BEGIN
                -- SET NOCOUNT ON added to prevent extra result sets from
                -- interfering with SELECT statements.

                UPDATE Empaque SET smartCardId = @SmartCardId,smartCard = @SmartCard
                WHERE id =(SELECT max(id) FROM Empaque 
                    WHERE serial=@Serial)

                SELECT @Filas=@@ROWCOUNT
            END
go

CREATE   PROCEDURE [dbo].[pa_UpdatePackingWeb]
@SerialId INT,
@SerialNuevo VARCHAR(50),
@NivelIdNuevo INT,
@Mac VARCHAR(50),
@SerialAnterior VARCHAR(50),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Empaque SET serialId=@SerialId,serial=@SerialNuevo,mac=@Mac,Nivelid=@NivelIdNuevo
	WHERE id =(SELECT max(id) FROM Empaque 
	WHERE serial=@SerialAnterior)
	select @Filas=@@ROWCOUNT
END

go


        CREATE PROCEDURE [dbo].[pa_UpdatePalletAccesory]
        @AccesorioId int,
        @PalletId int
        AS

        BEGIN
            UPDATE Accesorio SET palletId=@PalletId 
            WHERE id=@AccesorioId
        END
go

CREATE PROCEDURE [dbo].[pa_UpdatePalletAndTipologyEntry]
        @PalletId INT,
        @TipologyId INT,
        @UsuarioId INT,
        @EstadoId INT,
        @Serial VARCHAR(50)
        AS

        BEGIN
            UPDATE Ingreso SET palletId=@PalletId,TipologiaId=@TipologyId,EstadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
            WHERE serial=@Serial            
        END
go


CREATE PROCEDURE [dbo].[pa_UpdatePalletEntry]
@PalletId int,
@UsuarioId int,
@Serial varchar(50)
AS

BEGIN
    IF((SELECT cs.Codigo FROM pallet p 
    INNER JOIN CodigoSap cs ON p.CodigoSapId = cs.Id
    WHERE p.Id = @PalletId) <> 'MULTIMODELO')
        BEGIN
        	UPDATE Ingreso SET palletId=@PalletId,palletIdAlmacen=@PalletId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE(),loteId = (SELECT loteId FROM Pallet WHERE id =  @PalletId) 
	        WHERE serial=@Serial
        END
    ELSE
    BEGIN
        UPDATE Ingreso SET palletId=@PalletId,palletIdAlmacen=@PalletId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
        WHERE serial=@Serial
    END
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdatePart]
@EstadoId int,
@EstadoCosmeticaId int,
@UsuarioId int,
@ParteId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	update Parte set estadoId=@EstadoId,estadoCosmeticaId=@EstadoCosmeticaId,fechaMovimiento=GETDATE(),usuarioIdMovimiento=@UsuarioId 
	where id=@ParteId
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdatePositionPallet]
@PalletId int,
@PosicionId int,
@Filas int output
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    update Pallet set PosicionId=@PosicionId 
	where id=@PalletId
	select @Filas=@@ROWCOUNT
END




go

CREATE PROCEDURE [dbo].[pa_UpdatePqrs]
            @EstadoDiagnosticoId INT,
            @FallaDiagnosticoId INT,
            @Serial VARCHAR(50),
            @DiagnosticoTencnicoWoden VARCHAR(50)

            AS

            BEGIN
                -- SET NOCOUNT ON added to prevent extra result sets from
                -- interfering with SELECT statements.	

                DECLARE @DiagnosticoId INT
                DECLARE @SerialId INT

                SELECT TOP 1
                    @DiagnosticoId = id,
                    @SerialId = serialId
                FROM Diagnostico
                WHERE serial = @Serial
                ORDER BY id DESC

                UPDATE PQRS
                SET EstadoDiagnosticoId=@EstadoDiagnosticoId,
                    FalladiagnosticoId=@FallaDiagnosticoId,
                    FechaDiagnostico=GETDATE(),
                    x_studio_diagnstico_tcnico_woden=@DiagnosticoTencnicoWoden,
                    Id_diagnostico = @DiagnosticoId,
                    x_studio_fecha_reporte = GETDATE(),
                    id_stage = 4

                WHERE 
                    name = @Serial 
                AND 
                    SerialId = @SerialId
                    AND Id_diagnostico IS NULL
                    AND x_studio_diagnstico_tcnico_woden IS NULL
            END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdatePreDispatchEntry]
@EstadoId int,
@PalletId int,
@CajaEmpaqueId int,
@UsuarioId int,
@Serial varchar(50),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	UPDATE dbo.Ingreso SET estadoId=@EstadoId,palletId=@PalletId,palletIdEmpaque=@PalletId,cajaEmpaqueId=@CajaEmpaqueId,CajaDespachoId=0,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
	WHERE serial=@Serial
	
	SELECT @Filas=@@ROWCOUNT
END

go


CREATE PROCEDURE [dbo].[pa_UpdatePrealert]
@Nombre varchar(50),
@TipoOrigenId int,
@OrigenId int,
@Guia varchar(30),
@PrealertaId int,
@IdResponsable int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	update Prealerta set nombre=@Nombre,tipoOrigenId=@TipoOrigenId,origenId=@OrigenId,guia=@Guia,IdResponsable = @IdResponsable
	where id=@PrealertaId
	select @Filas=@@ROWCOUNT
END


go

CREATE PROCEDURE [dbo].[pa_UpdatePresupuesto]
        @Fecha DATE,
        @SegmentoId INT,
        @NivelId INT,
        @Unidades varchar(50),
        @Presupuesto VARCHAR(50),
        @Reclamo VARCHAR(50),
        @Dias varchar(50),
        @Tipo varchar(50),
        @Detalle VARCHAR(50),
        @Observaciones VARCHAR(50),
        @Id int,
        @Filas int output
        AS

        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.

            UPDATE Presupuesto SET fecha=@Fecha,segmentoId=@SegmentoId,nivelId=@NivelId,unidades=@Unidades,presupuesto=@Presupuesto,reclamo=@Reclamo,diasHabiles=@Dias,tipo=@Tipo,detalle=@Detalle,observaciones=@Observaciones
            WHERE id=@Id
            select @Filas=@@ROWCOUNT
        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateQuality]
@FallaFuncionalId INT,
@FallaCosmeticaId INT,
@Serial VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateQuality @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	UPDATE Calidad SET fallaFuncionalId=@FallaFuncionalId,fallaCosmeticaId=@FallaCosmeticaId 
	WHERE id=(SELECT max(id)id FROM Calidad 
	WHERE serial=@Serial)
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateQualityEntry]
@EstadoId int,
@UsuarioId int,
@Serial varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	UPDATE Ingreso SET estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
	WHERE serial=@Serial
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateQualityPalletEntry]
@EstadoId int,
@UsuarioId int,
@PalletId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	UPDATE Ingreso SET estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
	WHERE palletId=@PalletId
END




go

-- =============================================
-- Author:      Daniel Agudelo
-- Date:        2025-11-04
-- Description: Actualiza varios pallets de calidad en una sola ejecución
-- =============================================
CREATE   PROCEDURE [dbo].[pa_UpdateQualityPalletEntryBatch]
    @EstadoId INT,
    @UsuarioId INT,
    @PalletIds NVARCHAR(MAX) -- IDs separados por coma, ej: '1001,1002,1003'
AS
BEGIN
    SET NOCOUNT ON;

    -- Validación básica
    IF @PalletIds IS NULL OR LEN(@PalletIds) = 0
    BEGIN
        PRINT '⚠️ No se enviaron PalletIds válidos.';
        RETURN;
    END

    -- Convertir la lista CSV a tabla temporal
    DECLARE @Ids TABLE (palletId INT);

    INSERT INTO @Ids (palletId)
    SELECT TRY_CAST(value AS INT)
    FROM STRING_SPLIT(@PalletIds, ',')
    WHERE RTRIM(value) <> '' AND TRY_CAST(value AS INT) IS NOT NULL;

    -- Actualizar todos los pallets en una sola sentencia
    UPDATE I
    SET 
        I.estadoId = @EstadoId,
        I.usuarioIdMovimiento = @UsuarioId,
        I.fechaMovimiento = GETDATE()
    FROM Ingreso I
    INNER JOIN @Ids X ON I.palletId = X.palletId;

    -- Confirmar cantidad de registros afectados
    DECLARE @count INT = @@ROWCOUNT;
    PRINT CONCAT('✅ Pallets actualizados: ', @count);
END

go


CREATE PROCEDURE [dbo].[pa_UpdateRepair]
@EstadoFinalId int,
@Falla1Id int,
@Falla2Id int,
@Falla3Id int,
@Falla4Id int,
@PartesCambiadas varchar(150),
@MotivoScrapId int,
@TecnicoReparacionId int,
@Serial varchar(50),
@EstadoCalidadId int,
@Filas int output
AS

BEGIN
	DECLARE @ValFallaDxId int = 0;
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.	
	SELECT @ValFallaDxId = FallaDxId FROM Reparacion 
	WHERE Id = (SELECT max(r.id)id
	FROM Reparacion r
	INNER JOIN Maestro m ON r.estadoFinalId=m.id
	WHERE r.serial=@Serial
	AND m.codigo='ASIGNADO')

	IF @ValFallaDxId<>49 
	BEGIN

		UPDATE Reparacion
		SET estadoFinalId=@EstadoFinalId,
			falla1Id=@Falla1Id,
			falla2Id=@Falla2Id,
			falla3Id=@Falla3Id,
			falla4Id=@Falla4Id,
			partesCambiadas=@PartesCambiadas,
			MotivoScrapId=@MotivoScrapId,
			tecnicoReparacionId=@TecnicoReparacionId,
			fechaReparacion=GETDATE(),
			EstadoCalidadId=@EstadoCalidadId
		WHERE id=
			(SELECT max(r.id)id
			 FROM Reparacion r
			 INNER JOIN Maestro m ON r.estadoFinalId=m.id
			 WHERE r.serial=@Serial
			   AND m.codigo='ASIGNADO')
		SELECT @Filas = @@ROWCOUNT
	END
	ELSE
    BEGIN
        -- Caso 3: existe en PQRS y tipología distinta a 165
        RAISERROR('El Diagnostico del equipo no puede ser: SIN FALLA', 16, 1);
        RETURN;
    END
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateRepairEntry]
@SerialReal varchar(50),
@MacReal varchar(50),
@EstadoId int,
@UsuarioId int,
@Serial varchar(50),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ingreso SET serial=@SerialReal,mac=@MacReal,estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
	where serial=@Serial
	select @Filas=@@ROWCOUNT
END




go

CREATE PROCEDURE [dbo].[pa_UpdateSapCode]
@Codigo VARCHAR(30),
@Descripcion VARCHAR(100),
@FamiliaId INT,
@TipoId INT,
@Validacion INT,
@Direccion VARCHAR(20),
@Largos VARCHAR(50),
@Recorte INT,
@Reingreso INT,
@ClasificacionId INT,
@NumSerial INT,
@Id INT,
@Asignacion INT,
@LargosMac VARCHAR(50),
@LargosSerial3 VARCHAR(50),
@LargosSerial4 VARCHAR(50),
@LargosSerial5 VARCHAR(50),
@tipoEquipoId INT,
@areaId INT,
@ModeloId INT,
@ProveedorId INT,
@ValidacionMac INT,
@DireccionMac VARCHAR(20),
@RecorteMac INT,
@AsignacionFa INT,
@Multimodelo INT,
@CantidadCaja INT,
@Smartcard INT,
@Filas int output
AS
 
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
 
	UPDATE CodigoSap SET Codigo=@Codigo,Descripcion=@Descripcion,FamiliaId=@FamiliaId,TipoId=@TipoId,Validacion=@Validacion,Direccion=@Direccion,Largos=@Largos,Recorte=@Recorte,
	Reingreso=@Reingreso,ClasificacionId=@ClasificacionId,NumSerial=@NumSerial,AsignacionAcc=@Asignacion,LargosMac = @LargosMac,LargosSerial3=@LargosSerial3,LargosSerial4=@LargosSerial4,LargosSerial5=@LargosSerial5, tipoEquipoId = @tipoEquipoId,areaId = @areaId,
	modeloId = @ModeloId,proveedorId = @ProveedorId,validacionMac = @ValidacionMac,direccionMac = @DireccionMac,recorteMac = @RecorteMac, asignacionFalla= @AsignacionFa,multimodelo = @Multimodelo,cantidadCaja = @CantidadCaja,smartcard=@Smartcard
	WHERE Id=@Id
	select @Filas=@@ROWCOUNT
END
go


-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateSapCodeDiagnostic]
@Serial VARCHAR(50),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	UPDATE Diagnostico SET CodigoSapIdAnterior=(select CodigoSapId from Ingreso where Serial=@Serial) 
	WHERE id=(SELECT max(id)id FROM Diagnostico 
	WHERE serial=@Serial)
	select @Filas =  @@ROWCOUNT
END





go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateSapCodeEntry]
@CodigoSapId int,
@UsuarioId int,
@Serial varchar(50),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	UPDATE Ingreso SET codigoSapId=@CodigoSapId,fechaMovimiento=GETDATE(),usuarioIdMovimiento=@UsuarioId 
	WHERE serial=@Serial
	select @Filas=@@ROWCOUNT
END




go

CREATE PROCEDURE [dbo].[pa_UpdateScrapAll]
@Serial varchar(50),
@UsuarioId int,
@Novedad varchar(50),
@EstadoId int
AS
	BEGIN
	UPDATE Ingreso SET estadoId = @EstadoId, novedad = @Novedad, usuarioIdMovimiento = @UsuarioId, fechaMovimiento = GETDATE()
	WHERE serial=@Serial
END
go

CREATE PROCEDURE pa_UpdateSerialAccesory
            @SerialNuevo VARCHAR(50),
            @SerialAnterior VARCHAR(50),
            @Filas int output
            AS
            BEGIN

                UPDATE Accesorio SET serialEmpaque=@SerialNuevo 
                WHERE serialEmpaque=@SerialAnterior
                select @Filas=@@ROWCOUNT
            END
go

CREATE PROCEDURE [dbo].[pa_UpdateSerialIdAndSapCodeIdPqrs]
        @SerialId INT,
        @Serial VARCHAR(50),
        @CodigosapId INT,
        @Filas int output
        AS

        BEGIN

            DECLARE @FechaIngreso DATETIME
    
            
            SELECT @FechaIngreso = Fecha 
            FROM Ingreso 
            WHERE Id = @SerialId

            UPDATE PQRS 
            SET SerialId=@SerialId, 
            CodigosapId=@CodigosapId,
            x_studio_fecha_llegada_unidades=TRY_CAST(@FechaIngreso AS SMALLDATETIME)
            WHERE name = @Serial AND SerialId IS NULL
            select @Filas=@@ROWCOUNT
        END
go

CREATE PROCEDURE [dbo].[pa_UpdateSmartCard]
        @EstadoFinalId INT,
        @FallaId INT,
        @Serial VARCHAR(50)
        AS
        BEGIN           
            UPDATE SmartCard SET estadoFinalId=@EstadoFinalId,fallaId=@FallaId,Fecha=GETDATE() 
            WHERE id=(SELECT max(id)id FROM SmartCard
            WHERE serial=@Serial)
        END
go

CREATE PROCEDURE [dbo].[pa_UpdateSmartCardEntry]
            @SmartCardId INT,
            @SmartCard VARCHAR(50),
            @Serial VARCHAR(50),
            @Filas int output
            AS

            BEGIN
                -- SET NOCOUNT ON added to prevent extra result sets from
                -- interfering with SELECT statements.

                UPDATE Ingreso SET smartCardId = @SmartCardId,smartCard = @SmartCard                
                WHERE serial=@Serial

                SELECT @Filas=@@ROWCOUNT
            END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateStateAllEntry]
@EstadoId int,
@UsuarioId int,
@Serial varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ingreso SET estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
	WHERE serial=@Serial
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateStateBoxEntry]
@EstadoId int,
@UsuarioId int,
@CajaEmpaqueId int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ingreso SET estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
	WHERE cajaEmpaqueId=@CajaEmpaqueId
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateStateCiclic]
@CiclicoId INT,
@Estado VARCHAR(20),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateStateCiclic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	UPDATE Ciclico SET Estado=@Estado 
	WHERE Id=@CiclicoId
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateStateEntry]
@EstadoId int,
@PalletId int,
@UsuarioId int,
@Fecha int,
@Serial varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	IF(@Fecha=1)
	BEGIN
		UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
		WHERE serial=@Serial
	END
	ELSE
	BEGIN
		UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,usuarioIdMovimiento=@UsuarioId
		WHERE serial=@Serial
	END
	
END



go


    CREATE PROCEDURE [dbo].[pa_UpdateStateEntryBatch]
    @EstadoId int,
    @PalletId int,
    @UsuarioId int,
    @Fecha int,
    @Serial varchar(50),
    @LoteId INT
    AS
    /*
    DECLARE @PalletId INT
    SET @PalletId=0
    EXEC pa_UpdateDiagnostic @PalletId
    */
    BEGIN
        -- SET NOCOUNT ON added to prevent extra result sets from
        -- interfering with SELECT statements.
        IF(@Fecha=1)
        BEGIN
            UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() ,loteId=@LoteId
            WHERE serial=@Serial
        END
        ELSE
        BEGIN
            UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,usuarioIdMovimiento=@UsuarioId,loteId=@LoteId
            WHERE serial=@Serial
        END
        
    END
go

CREATE PROCEDURE [dbo].[pa_UpdateStateEntryNotUsuario]
	@EstadoId int,
	@PalletId int,
	@Fecha int,
	@Serial varchar(50)
	AS
	BEGIN
		-- SET NOCOUNT ON added to prevent extra result sets from
		-- interfering with SELECT statements.
		IF(@Fecha=1)
		BEGIN
			UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,fechaMovimiento=GETDATE() 
			WHERE serial=@Serial
		END
		ELSE
		BEGIN
			UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId
			WHERE serial=@Serial
		END		
	END
    
go

CREATE   PROCEDURE [dbo].[pa_UpdateStateEntryNotUsuarioDiagnosed]
	@EstadoId int,
	@PalletId int,
	@Fecha int,
	@Serial varchar(50)
	AS
	BEGIN
		-- SET NOCOUNT ON added to prevent extra result sets from
		-- interfering with SELECT statements.
		IF(@Fecha=1)
		BEGIN
			UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,
			usuarioIdMovimiento=isnull((select top 1 d.Usuarioid from diagnostico d
			where @Serial= d.serial order by d.Id DESC),UsuarioIdMovimiento),
			fechaMovimiento=GETDATE()
			WHERE serial=@Serial
		END
		ELSE
		BEGIN
			UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,
			usuarioIdMovimiento=isnull((select top 1 d.Usuarioid from diagnostico d
			where @Serial= d.serial order by d.Id DESC),UsuarioIdMovimiento)
			WHERE serial=@Serial
		END		
	END
go

CREATE   PROCEDURE [dbo].[pa_UpdateStateEntryNotUsuarioRepaired]
	@EstadoId int,
	@PalletId int,
	@Fecha int,
	@Serial varchar(50)
	AS
	BEGIN
		-- SET NOCOUNT ON added to prevent extra result sets from
		-- interfering with SELECT statements.
		IF(@Fecha=1)
		BEGIN
			UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,
			usuarioIdMovimiento=isnull((select top 1 r.TecnicoReparacionId from Reparacion r
		    where @Serial= r.serial order by r.Id DESC),UsuarioIdMovimiento),
			fechaMovimiento=GETDATE() 
			WHERE serial=@Serial
		END
		ELSE
		BEGIN
			UPDATE Ingreso SET estadoId=@EstadoId,palletId=@PalletId,
			usuarioIdMovimiento=isnull((select top 1 r.TecnicoReparacionId from Reparacion r
			where @Serial= r.serial order by r.Id DESC),UsuarioIdMovimiento)
			WHERE serial=@Serial
		END		
	END

	-----------------------------------------------------------------------------------------------------------------------

--Para Diagnostico
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateStateInventoryPallet]
@PalletId int,
@EstadoInventario int,
@Filas int output
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    update Pallet set EstadoInventario=@EstadoInventario 
	where id=@PalletId
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateStateOneEntry]
@EstadoId int,
@NivelId int,
@UsuarioId int,
@Fecha datetime,
@Serial varchar(50),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ingreso SET estadoId=@EstadoId,nivelId=@NivelId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
	WHERE serial=@Serial
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateStatePart]
@EstadoId int,
@PalletId int,
@UsuarioId int,
@ParteId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	update parte set estadoid=@EstadoId,palletId=@PalletId,usuarioIdmovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
	where id=@ParteId
END




go

CREATE PROCEDURE [dbo].[pa_UpdateStateRepairEntry]
@EstadoId int,
@UsuarioId int,
@PalletId int,
@Serial varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ingreso SET estadoId=@EstadoId,usuarioIdMovimiento=@UsuarioId,PalletId=@PalletId 
	WHERE serial=@Serial
END


go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateStatusAllBoxPacking]
@PalletId INT,
@EstadoId INT,
@Filas int output
AS
/*
DECLARE @PalletId INT
DECLARE @EstadoId INT
SET @PalletId=0
SET @EstadoId=0
EXEC pa_UpdateStatusAllBoxPacking @PalletIdId,@EstadoId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    UPDATE CajaEmpaque SET estadoId=@EstadoId
	WHERE palletId=@PalletId
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateStatusBoxPacking]
@CajaEmpaqueId INT,
@EstadoId INT,
@Filas int output
AS
/*
DECLARE @CajaEmpaqueId INT
DECLARE @EstadoId INT
SET @CajaEmpaqueId=0
SET @EstadoId=0
EXEC pa_updateStatusBoxPacking @CajaEmpaqueId,@EstadoId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    UPDATE CajaEmpaque SET estadoId=@EstadoId
	WHERE id=@CajaEmpaqueId
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateTipologyEntry]
@PalletId int,
@TipologiaId int,
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ingreso SET tipologiaId=@TipologiaId 
	WHERE palletId=@PalletId
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateTipologyPallet]
@PalletId int,
@TipologiaId int,
@Filas int output
AS
/*
DECLARE @Serial VARCHAR(50)
SET @Serial = '001'
EXEC pa_GetModelEntry @Serial
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

    update Pallet set tipologiaId=@TipologiaId 
	where id=@PalletId
	select @Filas=@@ROWCOUNT
END




go

CREATE PROCEDURE [dbo].[pa_UpdateTransportadora]
        @Nombre varchar(50),
        @Direccion varchar(120),
        @Telefono varchar(20),
        @Id int,
        @Filas int output
        AS

        BEGIN
            -- SET NOCOUNT ON added to prevent extra result sets from
            -- interfering with SELECT statements.

            UPDATE Transportadora SET nombre=@Nombre,direccion=@Direccion,telefono = @Telefono 
            WHERE id=@Id
            select @Filas=@@ROWCOUNT
        END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateUnificationEntry]
@EstadoId int,
@UsuarioId int,
@Serial varchar(50),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ingreso SET estadoId=@EstadoId,palletId=0,palletIdAlmacen=0,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE() 
	WHERE serial=@Serial
	select @Filas=@@ROWCOUNT
END




go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateUnreadable]
@SerialConfirmacion VARCHAR(50),
@MacConfirmacion VARCHAR(50),
@ConsecutivoSerial VARCHAR(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ilegible SET SerialConfirmacion=@SerialConfirmacion,MacConfirmacion=@MacConfirmacion 
	WHERE consecutivoSerial=@ConsecutivoSerial
END




go

 
CREATE PROCEDURE [dbo].[pa_UpdateUnreadableEntry]
    @SerialConfirmacion varchar(50),
    @MacConfirmacion varchar(50),
    @EstadoId int,
    @PalletId int,
    @UsuarioId int,
    @ConsecutivoSerial varchar(50)
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    IF (@PalletId = 99 AND @EstadoId = 99) -- Update desde Reparacion-Proceso / Ilegibles
    BEGIN
        UPDATE Ingreso
        SET serial = @SerialConfirmacion,
            mac = @MacConfirmacion,
            usuarioIdMovimiento = @UsuarioId,
            fechaMovimiento = GETDATE()
        WHERE serial = @ConsecutivoSerial
    END
    ELSE
    BEGIN
        UPDATE Ingreso
        SET serial = @SerialConfirmacion,
            mac = @MacConfirmacion,
            estadoId = @EstadoId,
            palletId = @PalletId,
            palletIdIngreso = @PalletId,
            usuarioIdMovimiento = @UsuarioId,
            fechaMovimiento = GETDATE()
        WHERE serial = @ConsecutivoSerial
    END
END
go


CREATE PROCEDURE [dbo].[pa_UpdateUnreadableEntryAdicional]
@SerialConfirmacion varchar(50),
@MacConfirmacion varchar(50),
@EstadoId int,
@PalletId int,
@UsuarioId int,
@ConsecutivoSerial varchar(50),
@Serial3Confirmacion varchar(50),
@Serial4Confirmacion varchar(50),
@Serial5Confirmacion varchar(50)
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.

	UPDATE Ingreso SET serial=@SerialConfirmacion,mac=@MacConfirmacion,estadoId=@EstadoId,palletId=@PalletId,palletIdIngreso=@PalletId,usuarioIdMovimiento=@UsuarioId,fechaMovimiento=GETDATE(),serial3=@Serial3Confirmacion,serial4=@Serial4Confirmacion,serial5=@Serial5Confirmacion  
	WHERE serial=@ConsecutivoSerial
END
go

-- =============================================
-- Author:		MEDINA DAVID
-- Create date: 20190916
-- Description:	TRAE LOS REDGISTROS DE INGRESO
-- =============================================
CREATE PROCEDURE [dbo].[pa_UpdateUnreadableRepair]
@SerialReal varchar(50),
@MacReal varchar(50),
@EstadoFinalId int,
@TecnicoReparacionId int,
@Serial varchar(50),
@Filas int output
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_GetModelSapCode @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	
	update Reparacion set serial=@SerialReal,mac=@MacReal,estadoFinalId=@EstadoFinalId,tecnicoReparacionId=@TecnicoReparacionId,fechaReparacion=GETDATE() 
	where id=(select max(r.id)id from Reparacion r 
	inner join Maestro m on r.estadoFinalId=m.id 
	where r.serial=@Serial and m.codigo='ASIGNADO')
	select @Filas=@@ROWCOUNT
END




go


CREATE PROCEDURE [dbo].[pa_UpdateWorflow]
    @ModuloId int,
    @OrigenId int,
    @OpcionId int,
    @TipologiaId int,
    @NivelId int,
    @Id int,
    @Filas int OUTPUT
AS
BEGIN
    UPDATE WorkFlow 
    SET moduloId = @ModuloId, origenId = @OrigenId, opcionId = @OpcionId, tipologiaId = @TipologiaId, nivelId = @NivelId 
    WHERE id = @Id;
    
    SELECT @Filas = @@ROWCOUNT;
END
go

CREATE PROCEDURE [dbo].[pa_deleteTables]
AS
/*
EXEC pa_deleteTables 
*/

BEGIN
	DELETE FROM Accesorio WHERE Id > 0
	DELETE FROM BaseDespacho WHERE Id > 0
	DELETE FROM BaseEmpaque WHERE Id > 0
	DELETE FROM BaseInventario WHERE Id > 0
	DELETE FROM BaseSeparacion WHERE Id > 0
	DELETE FROM BaseXLog WHERE Id > 0
	DELETE FROM CajaDespacho WHERE Id > 0
	DELETE FROM CajaEmpaque WHERE Id > 0
	DELETE FROM Calidad WHERE Id > 0
	DELETE FROM ciclico WHERE Id > 0
	DELETE FROM CiclicoPallet WHERE Id > 0
	DELETE FROM CiclicoSerial WHERE Id > 0
	DELETE FROM Despacho WHERE Id > 0
	DELETE FROM Diagnostico WHERE Id > 0
	DELETE FROM Empaque WHERE Id > 0
	DELETE FROM Ilegible WHERE Id > 0
	DELETE FROM Ingreso WHERE Id > 0
	DELETE FROM Inventario WHERE Id > 0
	DELETE FROM Movimiento WHERE Id > 0
	DELETE FROM Pallet WHERE Id > 0
	DELETE FROM PalletLog WHERE Id > 0
	DELETE FROM Parte WHERE Id > 0
	DELETE FROM Prealerta WHERE Id > 0
	DELETE FROM PrealertaSerial WHERE Id > 0
	DELETE FROM Reparacion WHERE Id > 0

	DBCC CHECKIDENT (Accesorio, RESEED,0)
	DBCC CHECKIDENT (BaseDespacho, RESEED,0)
	DBCC CHECKIDENT (BaseEmpaque, RESEED,0)
	DBCC CHECKIDENT (BaseInventario, RESEED,0)
	DBCC CHECKIDENT (BaseSeparacion, RESEED,0)
	DBCC CHECKIDENT (BaseXLog, RESEED,0)
	DBCC CHECKIDENT (CajaDespacho, RESEED,0)
	DBCC CHECKIDENT (CajaEmpaque, RESEED,0)
	DBCC CHECKIDENT (Calidad, RESEED,0)
	DBCC CHECKIDENT (ciclico, RESEED,0)
	DBCC CHECKIDENT (CiclicoPallet, RESEED,0)
	DBCC CHECKIDENT (CiclicoSerial, RESEED,0)
	DBCC CHECKIDENT (Despacho, RESEED,0)
	DBCC CHECKIDENT (Diagnostico, RESEED,0)
	DBCC CHECKIDENT (Empaque, RESEED,0)
	DBCC CHECKIDENT (Ilegible, RESEED,0)
	DBCC CHECKIDENT (Ingreso, RESEED,0)
	DBCC CHECKIDENT (Inventario, RESEED,0)
	DBCC CHECKIDENT (Movimiento, RESEED,0)
	DBCC CHECKIDENT (Pallet, RESEED,0)
	DBCC CHECKIDENT (Parte, RESEED,0)
	DBCC CHECKIDENT (Prealerta, RESEED,0)
	DBCC CHECKIDENT (PrealertaSerial, RESEED,0)
	DBCC CHECKIDENT (Reparacion, RESEED,0)
END


go

CREATE PROCEDURE [dbo].[pa_getDispatchBoxId]
        @PalletId int,
        @Numero int
        AS
        BEGIN
            SELECT Id
            FROM CajaDespacho WITH(NOLOCK) 
            WHERE PalletId=@PalletId and Numero=@Numero
        END
go

CREATE PROCEDURE [dbo].[pa_getEntryBoxId]
        @PalletId int,
        @Numero int
        AS
        BEGIN
            SELECT Id
            FROM CajaIngreso WITH(NOLOCK) 
            WHERE PalletId=@PalletId and Numero=@Numero
        END
go


CREATE PROCEDURE [dbo].[pa_getTipoOrigen]
@TipoOrigenId int
AS
/*
DECLARE @PalletId INT
SET @PalletId=0
EXEC pa_UpdateDiagnostic @PalletId
*/
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT Codigo AS TipoOrigen FROM Maestro WHERE Id=@TipoOrigenId 

END
go


create procedure [dbo].[pa_obtenerPrealertaById]
  @Id int
as
begin
   select  
          isnull(Id,0) Id,
          isnull(Nombre,'') Nombre,
          isnull(TipoOrigenId,0) TipoOrigenId,
          isnull(OrigenId,0) OrigenId,
          isnull(Guia,'') Guia,
          isnull(UsuarioId,0) UsuarioId,
          isnull(Fecha,'') Fecha,
          isnull(Estado,'') Estado,
          isnull(Activo,'') Activo,
          isnull(IdResponsable,0) IdResponsable
 from Prealerta where 
   Id = @Id
end 

go


    CREATE PROCEDURE [dbo].[pa_validateSmartCardInfo]
		@SmartCard varchar(50),
		@Filas int output
		AS
		BEGIN
			-- SET NOCOUNT ON added to prevent extra result sets from
			-- interfering with SELECT statements.
			
			set nocount on;
			SELECT Serial FROM Ingreso where smartCard = @SmartCard
			select @Filas=@@ROWCOUNT

		END
go

CREATE PROCEDURE[dbo].[sp_FamiliaAll]
AS
BEGIN
SELECT * FROM Maestro WHERE TipoMaestroId = 6
END
go

CREATE PROCEDURE [dbo].[sp_InformCalidadSalv]
AS

BEGIN
select c.serial,c.mac,cs.codigo as codigosap,fa.Codigo as Familia,cs.descripcion,p.numero as pallet,ce.numero as caja,ef.codigo as estadofinal,fc.codigo as fallacosmetica,ff.codigo as fallafuncional,u.nombreusuario as usuario,convert(date,c.fecha) as FECHA
 from Calidad c inner join CodigoSap cs on c.codigoSapId=cs.id 
inner join pallet p on c.palletId=p.id 
inner join CajaEmpaque ce on c.cajaEmpaqueId=ce.id 
inner join Maestro ef on c.estadoFinalId=ef.id 
left join Maestro fc on c.fallaCosmeticaId=fc.id 
left join Maestro ff on c.fallaFuncionalId=ff.id 
inner join Maestro fa on cs.FamiliaId = fa.Id
inner join WmsWdgeneral.dbo.usuario u on c.usuarioId=u.id
END
go

CREATE PROCEDURE [dbo].[sp_InformDespachoSalv]
AS
BEGIN
declare @status varchar(50)


begin
select distinct d.serial,d.mac,d.pedidosap as orden,fa.Codigo as Familia,p.numero as pallet,cd.numero as caja,cs.codigo as codigosap,cs.descripcion,convert(date,d.fecha)as Fecha,t.codigo as tipologia
from Despacho d 
inner join Pallet p on d.palletId=p.id 
left join CajaDespacho cd on d.cajaDespachoId=cd.id 
inner join CodigoSap cs on d.codigoSapId=cs.id 
inner join Maestro fa on cs.FamiliaId = fa.Id
inner join Maestro t on p.tipologiaId=t.id
inner join WmsWdGeneral.dbo.estado e on d.estadoId=e.id 

end

begin
select distinct i.serial,i.mac,'' as orden,p.numero as pallet,cd.numero as caja,cs.codigo as codigosap,cs.descripcion,i.fechaMovimiento as fecha,t.codigo as tipologia from Ingreso i
inner join Pallet p on i.palletId=p.id 
left join CajaDespacho cd on i.cajaDespachoId=cd.id 
inner join CodigoSap cs on i.codigoSapId=cs.id 
inner join Maestro fa on cs.FamiliaId = fa.Id
inner join Maestro t on p.tipologiaId=t.id
inner join WMsWdGeneral.dbo.estado e on i.estadoId=e.id 

end
END

go

CREATE PROCEDURE [dbo].[sp_InformDiagnosSalv]
AS

BEGIN
select d.serial,d.mac,cs.codigo as codigosap,cs.descripcion,f.codigo+'|'+f.descripcion as falla,e.codigo 
as estadoFinal,d.fecha,u.nombreusuario as usuario, fa.Codigo as Familia,ori.Codigo as Origen 
from Diagnostico d 
inner join CodigoSap cs on d.codigoSapId=cs.id 
inner join Maestro f on d.fallaId=f.id
inner join Maestro e on d.estadoFinalId=e.id 
inner join Maestro fa on cs.FamiliaId = fa.Id
inner join Ingreso i on i.serial = d.serial 
inner join Maestro ori on i.OrigenId = ori.id
inner join  WmsWdGeneral.dbo.usuario u on d.usuarioId=u.id 

union 
select d.serial,d.mac,cs.codigo as codigosap,cs.descripcion,f.codigo+'|'+f.descripcion as falla,e.codigo 
as estadoFinal,d.fecha,u.nombreusuario as usuario, fa.Codigo as Familia,ori.Codigo as Origen 
from Diagnostico d 
inner join CodigoSap cs on d.codigoSapId=cs.id 
inner join Maestro f on d.fallaId=f.id
inner join Maestro e on d.estadoFinalId=e.id 
inner join Maestro fa on cs.FamiliaId = fa.Id
inner join Despacho i on i.serial = d.serial 
inner join Maestro ori on i.OrigenId = ori.id
inner join  WmsWdGeneral.dbo.usuario u on d.usuarioId=u.id 
END
go

CREATE PROCEDURE [dbo].[sp_InformEmpaqueSalv]
AS
BEGIN
select e.serial,e.mac,cs.codigo as codigosap,cs.descripcion,p.numero as pallet,ce.numero as caja,u.nombreusuario as usuario,fa.Codigo as Familia,e.fecha,
n.codigo as nivel,t.codigo as tipologia from Empaque e 
inner join Pallet p on e.palletId=p.id 
inner join CajaEmpaque ce on e.cajaEmpaqueId=ce.id 
inner join CodigoSap cs on e.codigoSapId=cs.id 
inner join Maestro fa on cs.FamiliaId = fa.Id
inner join WmsWdGeneral.dbo.usuario u on e.usuarioId=u.id 
inner join Maestro n on e.nivelId=n.id 
inner join Maestro t on p.tipologiaId=t.id 
END
go


CREATE PROCEDURE [dbo].[sp_InformIngresoSalv]
AS


BEGIN
select * from(select distinct convert(date,i.Fecha)as Fecha,i.Serial,i.Mac,t.Codigo as Tipologia,cs.Codigo as CodigoSap,cs.Descripcion,
p.Numero as Pallet,u.NombreUsuario,i.Documento,i.Guia,ti.Codigo as TipoOrigen,o.Codigo as Origen,fa.Codigo as Familia,e.Nombre as estado 
from dbo.Ingreso i with(nolock)
INNER JOIN dbo.CodigoSap cs with(nolock) on i.CodigoSapId = cs.Id
INNER JOIN dbo.Pallet p with(nolock) on i.PalletIdIngreso = p.Id
INNER JOIN dbo.Usuario u with(nolock) on i.UsuarioId = u.Id
INNER JOIN dbo.Maestro t with(nolock) on i.TipologiaId = t.Id
INNER JOIN dbo.Maestro ti with(nolock) on i.TipoOrigenId = ti.Id
inner join Maestro fa on cs.FamiliaId = fa.Id
INNER JOIN dbo.Maestro o with(nolock) on i.OrigenId = o.Id 
INNER JOIN dbo.Estado e with(nolock) on i.EstadoId = e.Id 

 union
select  distinct convert(date,i.FechaIngreso)as Fecha,i.Serial,i.Mac,t.Codigo as Tipologia,cs.Codigo as CodigoSap,cs.Descripcion,
p.Numero as Pallet,u.NombreUsuario,i.Documento,i.Guia,ti.Codigo as TipoOrigen,o.Codigo as Origen,fa.Codigo as Familia,e.Nombre as estado 
from dbo.Despacho i with(nolock)
INNER JOIN dbo.CodigoSap cs with(nolock) on i.CodigoSapId = cs.Id
INNER JOIN dbo.Pallet p with(nolock) on i.PalletIdIngreso = p.Id
INNER JOIN dbo.Usuario u with(nolock) on i.UsuarioId = u.Id
INNER JOIN dbo.Maestro t with(nolock) on i.TipologiaId = t.Id
INNER JOIN dbo.Maestro ti with(nolock) on i.TipoOrigenId = ti.Id
inner join Maestro fa on cs.FamiliaId = fa.Id
INNER JOIN dbo.Maestro o with(nolock) on i.OrigenId = o.Id 
INNER JOIN dbo.Estado e with(nolock) on i.EstadoId = e.Id 
)as Ingreso 
order by Fecha
END
go

CREATE PROCEDURE [dbo].[sp_InformLabReverseSal]
AS

begin

	select MONTH(e.Fecha)as 'MES PRODUCCION',convert(date,e.Fecha)as 'FECHA PRODUCCION',cs.Codigo as 'CODIGO MODELO',cs.Descripcion as 'MODELO',CONVERT(varchar(50),'') as Origen,e.SerialId,e.serial as 'SERIE PRINCIPAL',
	f.Codigo as Categoria,e.NivelId,convert(varchar(20),concat('NIVEL ',convert(int,n.Codigo)-1)) as 'NIVEL REPARACION',convert(varchar(50),'')as Diagnostico,
	ISNULL(t.precio,0) as Tarifa,'El SALVADOR' as Proyecto
	into #EmpaqueSal
	from WmsWdTigoSal.dbo.Empaque e
	left join WmsWdTigoSal.dbo.Maestro n on e.NivelId = n.Id 
	inner join WmsWdTigoSal.dbo.CodigoSap cs on e.CodigoSapId=cs.Id 
	inner join WmsWdTigoSal.dbo.Maestro f on cs.FamiliaId=f.Id 
	left join WmsWdTigoSal.dbo.Tarifa t on e.NivelId=t.nivelId  

	select MONTH(r.FechaReparacion)as 'MES PRODUCCION',convert(date,r.FechaReparacion)as 'FECHA PRODUCCION',cs.Codigo as 'CODIGO MODELO',cs.Descripcion as 'MODELO',case when ISNULL(oi.Codigo,'')='' then od.Codigo else oi.Codigo end as Origen,r.SerialId,r.serial as 'SERIE PRINCIPAL',
	f.Codigo as Categoria,case when ISNULL(i.NivelId,'')='' then d.NivelId else i.NivelId end as NivelId,'SCRAP' as 'NIVEL REPARACION',ms.Codigo+'|'+ms.Descripcion as Diagnostico,
	case when ISNULL(ti.precio,'0')='0' then td.precio else ti.precio end as Tarifa,'EL SALVADOR' as Proyecto
	into #SerializadoScrapSal
	from WmsWdTigoSal.dbo.Reparacion r 
	inner join WmsWdTigoSal.dbo.CodigoSap cs on r.CodigoSapId=cs.Id 
	inner join WmsWdTigoSal.dbo.Maestro ms on r.MotivoScrapId=ms.Id 
	inner join WmsWdTigoSal.dbo.Maestro f on cs.FamiliaId=f.Id 
	left join WmsWdTigoSal.dbo.Ingreso i on r.SerialId=i.Id 
	left join WmsWdTigoSal.dbo.Maestro oi on i.OrigenId=oi.Id 
	left join WmsWdTigoSal.dbo.Tarifa ti on i.NivelId=ti.nivelId 
	left join WmsWdTigoSal.dbo.Despacho d on r.SerialId=d.SerialId 
	left join WmsWdTigoSal.dbo.Maestro od on d.OrigenId=od.Id 
	left join WmsWdTigoSal.dbo.Tarifa td on d.NivelId=td.nivelId 
	update #EmpaqueSal
	set Origen = isnull(m.Codigo,md.Codigo)
	from #EmpaqueSal e
	left join WmsWdTigoSal.dbo.Ingreso i on e.SerialId = i.Id
	left join WmsWdTigoSal.dbo.Maestro m on i.OrigenId = m.Id 
	left join WmsWdTigoSal.dbo.Despacho d on e.SerialId = d.SerialId
	left join WmsWdTigoSal.dbo.Maestro md on d.OrigenId = md.Id 
	select e.[SERIE PRINCIPAL],MAX(Fecha)as fecha,fd.Codigo,fd.Codigo+'|'+fd.Descripcion as Falla 
	into #DiagnosticoSal from #EmpaqueSal e 
	inner join WmsWdTigoSal.dbo.Diagnostico d on e.SerialId=d.SerialId 
	inner join WmsWdTigoSal.dbo.Maestro fd on d.FallaId=fd.Id 
	where e.[NIVEL REPARACION] in('NIVEL 0','NIVEL 1') and d.Fecha=(select max(fecha) from WmsWdTigoSal.dbo.Diagnostico where SerialId=e.SerialId)
	group by e.[SERIE PRINCIPAL],fd.Codigo,fd.Descripcion 
	order by e.[SERIE PRINCIPAL]
	update #EmpaqueSal 
	set Diagnostico =d.Falla 
	from #EmpaqueSal e 
	inner join #DiagnosticoSal d on e.[SERIE PRINCIPAL]=d.[SERIE PRINCIPAL]
	select e.[SERIE PRINCIPAL],MAX(r.FechaReparacion)as fecha,fr.Codigo+'|'+fr.Descripcion as Falla 
	into #ReparacionSal from #EmpaqueSal e 
	inner join WmsWdTigoSal.dbo.Reparacion r on e.SerialId=r.SerialId 
	inner join WmsWdTigoSal.dbo.Maestro fr on r.Falla1Id=fr.Id 
	where e.[NIVEL REPARACION] ='NIVEL 2' and r.FechaReparacion=(select max(FechaReparacion) from WmsWdTigoSal.dbo.Reparacion where SerialId=e.SerialId)
	group by e.[SERIE PRINCIPAL],fr.Codigo,fr.Descripcion 
	order by e.[SERIE PRINCIPAL]
	update #EmpaqueSal 
	set Diagnostico =r.Falla 
	from #EmpaqueSal e 
	inner join #ReparacionSal r on e.[SERIE PRINCIPAL]=r.[SERIE PRINCIPAL]
	select * from #EmpaqueSal union all select * from #SerializadoScrapSal
	drop table #EmpaqueSal
	drop table #SerializadoScrapSal
	drop table #DiagnosticoSal
	drop table #ReparacionSal
end
go

CREATE PROCEDURE [dbo].[sp_InformReparacionSalv]
AS
BEGIN
select r.serial,r.mac,ori.Codigo as Origen,cs.codigo as codigosap,fa.Codigo as Familia,cs.descripcion,fd.codigo+'|'+fd.descripcion as fallaDiagnostico,f1.codigo+'|'+f1.descripcion as falla1,f2.codigo+'|'+f2.descripcion as falla2,f3.codigo+'|'+f3.descripcion as falla3,f4.codigo+'|'+f4.descripcion as falla4,e.codigo as estadoFinal,m.codigo as motivoScrap,tr.nombreUsuario as tecnicoReparacion,r.fechaReparacion from Reparacion r 
inner join CodigoSap cs on r.codigoSapId=cs.id 
inner join Ingreso i on i.serial = r.serial 
inner join Maestro ori on i.OrigenId = ori.id
left join Maestro fd on r.fallaDxId=fd.id 
inner join Maestro f1 on r.falla1Id=f1.id 
left join Maestro f2 on r.falla2Id=f2.id 
left join Maestro f3 on r.falla3Id=f3.id 
left join Maestro f4 on r.falla4Id=f4.id 
inner join Maestro e on r.estadoFinalId=e.id 
left join Maestro m on r.motivoScrapId=m.id 
inner join Maestro fa on cs.FamiliaId = fa.Id
inner join WmsWdgeneral.dbo.usuario tr on r.tecnicoReparacionId=tr.id 
union 
select r.serial,r.mac,ori.Codigo as Origen,cs.codigo as codigosap,fa.Codigo as Familia,cs.descripcion,fd.codigo+'|'+fd.descripcion as fallaDiagnostico,f1.codigo+'|'+f1.descripcion as falla1,f2.codigo+'|'+f2.descripcion as falla2,f3.codigo+'|'+f3.descripcion as falla3,f4.codigo+'|'+f4.descripcion as falla4,e.codigo as estadoFinal,m.codigo as motivoScrap,tr.nombreUsuario as tecnicoReparacion,r.fechaReparacion from Reparacion r 
inner join CodigoSap cs on r.codigoSapId=cs.id 
inner join Despacho i on i.serial = r.serial 
inner join Maestro ori on i.OrigenId = ori.id
left join Maestro fd on r.fallaDxId=fd.id 
inner join Maestro f1 on r.falla1Id=f1.id 
left join Maestro f2 on r.falla2Id=f2.id 
left join Maestro f3 on r.falla3Id=f3.id 
left join Maestro f4 on r.falla4Id=f4.id 
inner join Maestro e on r.estadoFinalId=e.id 
left join Maestro m on r.motivoScrapId=m.id 
inner join Maestro fa on cs.FamiliaId = fa.Id
inner join WmsWdgeneral.dbo.usuario tr on r.tecnicoReparacionId=tr.id

END
go

CREATE PROCEDURE [dbo].[sp_InformWipSalv]
AS

BEGIN
select i.Serial,i.mac,cs.Codigo as codigosap,cs.Descripcion,
f.Codigo as familia,c.Codigo as tipologia,isnull(e.nombre,'') as localizacion,
isnull(p.Numero,'') as pallet,isnull(po.Numero,'') as posicion,n.Codigo as nivel,
i.Fecha as fechaIngreso,i.FechaMovimiento,tor.Codigo as tipoOrigen,
o.Codigo as origen,isnull(op.Nombre,'') as origenPallet,i.GarantiaFabricante,
t.Codigo as estado,isnull(TotalIngresos,0) as TotalIngresos 
from dbo.Ingreso i with(nolock) 
left join dbo.CodigoSap cs with(nolock) on i.CodigoSapId=cs.id 
left join dbo.Maestro f with(nolock) on cs.FamiliaId=f.Id 
left join dbo.Maestro c with(nolock) on cs.ClasificacionId=c.id 
left join dbo.Estado e with(nolock) on i.EstadoId=e.id 
left join dbo.Pallet p with(nolock) on i.PalletId=p.id 
left join dbo.Posicion po with(nolock) on p.PosicionId=po.id 
left join dbo.Maestro n with(nolock) on i.NivelId=n.id 
left join dbo.Maestro tor with(nolock) on i.TipoOrigenId=tor.id 
left join dbo.Maestro o with(nolock) on i.OrigenId=o.id 
left join dbo.Estado op with(nolock) on p.OrigenId=op.id 
left join dbo.Maestro t with(nolock) on i.TipologiaId=t.id 
left join
(select serial,sum(cantidad)TotalIngresos from 
(select serial,count(serial)cantidad from dbo.Ingreso with(nolock) group by serial union all 
select serial,count(serial)cantidad from dbo.Despacho with(nolock) group by serial)as tabla group by serial)as tabla2 on i.Serial=tabla2.Serial
END
go

CREATE PROCEDURE sp_ReporteHeadCountTelefonica
AS
BEGIN
    SET NOCOUNT ON;

    WITH DatosUsuarios AS (
        SELECT CAST(Fecha AS DATE) AS Fecha, UsuarioId, 'Ingreso' AS Proceso, 'Telefonica' AS Cliente
        FROM WmsWdTelefonicaColombiaBOG.dbo.Ingreso
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Despacho', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Despacho
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(FechaReparacion AS DATE), UsuarioId, 'Reparacion', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Reparacion
        WHERE FechaReparacion >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Clasificacion', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Clasificacion
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Calidad', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Calidad
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Diagnostico', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Diagnostico
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Cosmetica', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Cosmetica
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Empaque', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Empaque
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Inventario', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Inventario
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Ensamble', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Ensamble
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Etiquetado', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Etiquetado
        WHERE Fecha >= '2024-01-01'
    )

    SELECT 
        Fecha,
        Cliente,
        'HeadCount' AS Detalle,
        COUNT(DISTINCT CASE WHEN Proceso = 'Ingreso' THEN UsuarioId END) AS Ingreso,
        COUNT(DISTINCT CASE WHEN Proceso = 'Despacho' THEN UsuarioId END) AS Despacho,
        COUNT(DISTINCT CASE WHEN Proceso = 'Reparacion' THEN UsuarioId END) AS Reparacion,
        COUNT(DISTINCT CASE WHEN Proceso = 'Clasificacion' THEN UsuarioId END) AS Clasificacion,
        COUNT(DISTINCT CASE WHEN Proceso = 'Calidad' THEN UsuarioId END) AS Calidad,
        COUNT(DISTINCT CASE WHEN Proceso = 'Diagnostico' THEN UsuarioId END) AS Diagnostico,
        COUNT(DISTINCT CASE WHEN Proceso = 'Cosmetica' THEN UsuarioId END) AS Cosmetica,
        COUNT(DISTINCT CASE WHEN Proceso = 'Empaque' THEN UsuarioId END) AS Empaque,
        COUNT(DISTINCT CASE WHEN Proceso = 'Inventario' THEN UsuarioId END) AS Inventario,
        COUNT(DISTINCT CASE WHEN Proceso = 'Ensamble' THEN UsuarioId END) AS Ensamble,
        COUNT(DISTINCT CASE WHEN Proceso = 'Etiquetado' THEN UsuarioId END) AS Etiquetado
    FROM DatosUsuarios
    GROUP BY Fecha, Cliente
    ORDER BY Fecha, Cliente;
END;
go

CREATE PROCEDURE sp_ReporteHeadCountTelefonicaTableros
AS
BEGIN
    SET NOCOUNT ON;

    WITH DatosUsuarios AS (
        SELECT CAST(Fecha AS DATE) AS Fecha, UsuarioId, 'Ingreso' AS Proceso, 'Telefonica' AS Cliente
        FROM WmsWdTelefonicaColombiaBOG.dbo.Ingreso
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Despacho', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Despacho
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(FechaReparacion AS DATE), UsuarioId, 'Reparacion', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Reparacion
        WHERE FechaReparacion >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Clasificacion', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Clasificacion
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Calidad', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Calidad
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Diagnostico', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Diagnostico
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Cosmetica', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Cosmetica
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Empaque', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Empaque
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Inventario', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Inventario
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Ensamble', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Ensamble
        WHERE Fecha >= '2024-01-01'

        UNION ALL

        SELECT CAST(Fecha AS DATE), UsuarioId, 'Etiquetado', 'Telefonica'
        FROM WmsWdTelefonicaColombiaBOG.dbo.Etiquetado
        WHERE Fecha >= '2024-01-01'
    )

    SELECT 
        Fecha,
        Cliente,
        'HeadCount' AS Detalle,
        COUNT( CASE WHEN Proceso = 'Ingreso' THEN UsuarioId END) AS Ingreso,
        COUNT( CASE WHEN Proceso = 'Despacho' THEN UsuarioId END) AS Despacho,
        COUNT( CASE WHEN Proceso = 'Reparacion' THEN UsuarioId END) AS Reparacion,
        COUNT( CASE WHEN Proceso = 'Clasificacion' THEN UsuarioId END) AS Clasificacion,
        COUNT( CASE WHEN Proceso = 'Calidad' THEN UsuarioId END) AS Calidad,
        COUNT( CASE WHEN Proceso = 'Diagnostico' THEN UsuarioId END) AS Diagnostico,
        COUNT( CASE WHEN Proceso = 'Cosmetica' THEN UsuarioId END) AS Cosmetica,
        COUNT( CASE WHEN Proceso = 'Empaque' THEN UsuarioId END) AS Empaque,
        COUNT( CASE WHEN Proceso = 'Inventario' THEN UsuarioId END) AS Inventario,
        COUNT( CASE WHEN Proceso = 'Ensamble' THEN UsuarioId END) AS Ensamble,
        COUNT( CASE WHEN Proceso = 'Etiquetado' THEN UsuarioId END) AS Etiquetado
    FROM DatosUsuarios
    GROUP BY Fecha, Cliente
    ORDER BY Fecha, Cliente;
END;
go

