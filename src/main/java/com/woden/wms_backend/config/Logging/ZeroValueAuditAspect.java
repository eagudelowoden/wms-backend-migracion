package com.woden.wms_backend.config.Logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Set;

/**
 * Audita en tiempo real las llamadas a los repositorios de ClienteRepositories
 * (SPs de Ingreso, Pallet, Empaque, Despacho, etc.) y deja un WARN cuando algún
 * campo "crítico" (que nunca debería guardarse en 0) llega en 0.
 *
 * Para vigilar un campo nuevo, solo hay que agregar su nombre (tal como aparece
 * en el @Param del repositorio, en minúsculas) a CAMPOS_VIGILADOS.
 */
@Aspect
@Component
public class ZeroValueAuditAspect {

  private static final Logger logger = LoggerFactory.getLogger(ZeroValueAuditAspect.class);

  private static final Set<String> CAMPOS_VIGILADOS = Set.of(
      "usuarioidmovimiento",
      "usuarioid",
      "estadoid"
  );

  @Around("execution(* com.woden.wms_backend.repositories.ClienteRepositories..*.*(..))")
  public Object auditarValoresEnCero(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    Parameter[] parametros = method.getParameters();
    Object[] argumentos = joinPoint.getArgs();

    for (int i = 0; i < parametros.length && i < argumentos.length; i++) {
      String nombreParametro = obtenerNombreParametro(parametros[i]);
      if (nombreParametro == null || !CAMPOS_VIGILADOS.contains(nombreParametro.toLowerCase())) {
        continue;
      }

      Object valor = argumentos[i];
      if (valor instanceof Integer && (Integer) valor == 0) {
        logger.warn(
            "[ZeroValueAudit] {}.{} — parámetro '{}' llega en 0. Argumentos: {}",
            method.getDeclaringClass().getSimpleName(),
            method.getName(),
            nombreParametro,
            Arrays.toString(argumentos));
      }
    }

    return joinPoint.proceed();
  }

  private String obtenerNombreParametro(Parameter parametro) {
    Param paramAnnotation = parametro.getAnnotation(Param.class);
    if (paramAnnotation != null) {
      return paramAnnotation.value();
    }
    return parametro.isNamePresent() ? parametro.getName() : null;
  }
}
