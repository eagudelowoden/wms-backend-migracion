package com.woden.wms_backend.config.DataSource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;

@Aspect
@Component
public class DatabaseRoutingAspect {

  // Intercepta todas las llamadas a los métodos en los paquetes de WmsWdGeneral
  @Around("execution(* com.woden.wms_backend.repositories.WmsWdGeneral..*.*(..)) || " +
      "execution(* com.woden.wms_backend.services.WmsWdGeneral..*.*(..)) || " +
      "execution(* com.woden.wms_backend.controllers.WmsWdGeneral..*.*(..))")
  public Object useGeneralDatabaseForWmsWdGeneralComponents(ProceedingJoinPoint joinPoint) throws Throwable {
    // Verificar si el método debe estar excluido
    if (shouldExcludeMethod(joinPoint)) {
      return joinPoint.proceed();
    }

    // Guarda el estado actual
    boolean previousState = ClientDatabaseContext.isUsingGeneralDb();

    try {
      // Fuerza el uso de la base de datos general
      ClientDatabaseContext.useGeneralDatabase();

      // Ejecuta el método original
      return joinPoint.proceed();
    } finally {
      // Restaura el estado anterior si es necesario
      if (!previousState) {
        ClientDatabaseContext.resetGeneralDatabaseOverride();
      }
    }
  }

  private boolean shouldExcludeMethod(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    // Excluir endpoints específicos relacionados con la verificación de conexiones
    if (method.getDeclaringClass().getSimpleName().equals("DataBaseController")) {
      if (method.getName().equals("getCurrentConnections")) {
        return true;
      }
    }

    // También puedes excluir por anotaciones
    GetMapping getMapping = method.getAnnotation(GetMapping.class);
    if (getMapping != null) {
      for (String path : getMapping.value()) {
        if (path.contains("/current-connections") || path.contains("/switch-client")) {
          return true;
        }
      }
    }

    PostMapping postMapping = method.getAnnotation(PostMapping.class);
    if (postMapping != null) {
      for (String path : postMapping.value()) {
        if (path.contains("/switch-client")) {
          return true;
        }
      }
    }

    return false;
  }
}