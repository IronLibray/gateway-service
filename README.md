# 🌉 Gateway Service - Iron Library

> API Gateway y punto de entrada único para todos los microservicios de Iron Library

## 🎯 Descripción

Este microservicio actúa como el **punto de entrada único** para toda la arquitectura distribuida de Iron Library. Basado en Spring Cloud Gateway, enruta todas las peticiones HTTP a los microservicios correspondientes, proporcionando funcionalidades como balanceo de carga, filtros, y control de acceso centralizado.

## 🚀 Características

- ✅ **Enrutamiento inteligente** a microservicios
- ✅ **Balanceo de carga** automático
- ✅ **Filtros personalizables** para peticiones/respuestas
- ✅ **Descubrimiento de servicios** con Eureka
- ✅ **Health checks** agregados
- ✅ **CORS** configurado para desarrollo
- ✅ **Logging** centralizado de peticiones
- ✅ **Failover** automático

## 🛠️ Stack Tecnológico

- **Spring Boot** 3.4.6
- **Spring Cloud Gateway** - API Gateway
- **Spring Cloud Netflix Eureka Client** - Service Discovery
- **Spring Boot Actuator** - Monitoreo y métricas
- **Java** 21
- **Maven** 3.9.9

## 📡 Rutas Configuradas

### Base URL: `http://localhost:8080`

| Ruta | Destino | Descripción |
|------|---------|-------------|
| `/api/books/**` | `book-service` | Gestión de libros y catálogo |
| `/api/users/**` | `user-service` | Gestión de usuarios y membresías |
| `/api/loans/**` | `loan-service` | Gestión de préstamos |
| `/health` | `gateway-health` | Health check del gateway |
| `/actuator/**` | `actuator` | Endpoints de monitoreo |

### Ejemplo de Uso
```bash
# Obtener todos los libros (a través del gateway)
curl http://localhost:8080/api/books

# Crear un usuario (a través del gateway)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Juan", "email": "juan@email.com"}'

# Crear un préstamo (a través del gateway)
curl -X POST http://localhost:8080/api/loans/quick?userId=1&bookId=1
```

## 🔧 Configuración

### Variables de Entorno
```properties
# Configuración del gateway
spring.application.name=gateway-service
server.port=8080

# Eureka
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/

# Actuator endpoints
management.endpoints.web.exposure.include=health,gateway,info
management.endpoint.health.show-details=always
```

### Configuración de Rutas (GatewayConfig.java)
```java
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                
                // Ruta para Book Service
                .route("book-service", r -> r
                        .path("/api/books/**")
                        .uri("lb://book-service"))

                // Ruta para User Service
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("lb://user-service"))

                // Ruta para Loan Service
                .route("loan-service", r -> r
                        .path("/api/loans/**")
                        .uri("lb://loan-service"))

                // Ruta para health check del gateway
                .route("gateway-health", r -> r
                        .path("/health")
                        .uri("forward:/actuator/health"))

                .build();
    }
}
```

## 🚀 Instalación y Ejecución

### Prerrequisitos
- Java 21
- Maven 3.6+
- Discovery Server ejecutándose en puerto 8761
- Microservicios registrados en Eureka

### Pasos de Instalación
```bash
# Clonar el repositorio
git clone https://github.com/IronLibrary/gateway-service.git
cd gateway-service

# Instalar dependencias
./mvnw clean install

# Ejecutar el gateway
./mvnw spring-boot:run
```

### Verificar Instalación
```bash
# Health check del gateway
curl http://localhost:8080/health

# Verificar rutas configuradas
curl http://localhost:8080/actuator/gateway/routes

# Probar enrutamiento a un servicio
curl http://localhost:8080/api/books/health
```

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests
./mvnw test

# Test de contexto
./mvnw test -Dtest="*ApplicationTests"
```

### Pruebas de Enrutamiento
```bash
# Probar todas las rutas
curl http://localhost:8080/api/books
curl http://localhost:8080/api/users  
curl http://localhost:8080/api/loans

# Verificar balanceo de carga (si hay múltiples instancias)
for i in {1..5}; do
  curl http://localhost:8080/api/books/health
done
```

## 🔗 Integración con Servicios

### Descubrimiento de Servicios
El gateway utiliza Eureka para descubrir automáticamente los servicios:
- **lb://book-service** → Resuelve a instancias de book-service
- **lb://user-service** → Resuelve a instancias de user-service  
- **lb://loan-service** → Resuelve a instancias de loan-service

### Load Balancing
Spring Cloud Gateway incluye balanceo de carga automático:
- **Round Robin** (por defecto)
- **Least Connections** (configurable)
- **Random** (configurable)

## 📊 Monitoreo y Observabilidad

### Actuator Endpoints
```bash
# Health check completo
GET /actuator/health

# Estado de rutas configuradas
GET /actuator/gateway/routes

# Métricas del gateway
GET /actuator/metrics

# Información general
GET /actuator/info
```

### Respuesta de Health Check
```json
{
  "status": "UP",
  "components": {
    "eureka": {
      "status": "UP",
      "details": {
        "applications": {
          "BOOK-SERVICE": 1,
          "USER-SERVICE": 1,
          "LOAN-SERVICE": 1
        }
      }
    },
    "gateway": {
      "status": "UP"
    }
  }
}
```

### Métricas Disponibles
- Tiempo de respuesta por ruta
- Número de peticiones por servicio
- Errores y reintentos
- Estado de servicios downstream

## 🔒 Configuración de Seguridad

### CORS (Desarrollo)
```java
// Configuración actual - permite todos los orígenes
@CrossOrigin(origins = "*")
```

### Producción (Recomendado)
```java
@Bean
public CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOriginPatterns(List.of("https://yourdomain.com"));
    corsConfig.setMaxAge(8000L);
    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    corsConfig.setAllowedHeaders(List.of("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
}
```

## 🚀 Filtros Personalizados

### Filtros Globales (Ejemplo)
```java
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Request: {} {}", request.getMethod(), request.getURI());
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            log.info("Response status: {}", response.getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
```

### Filtros por Ruta
```java
.route("book-service", r -> r
    .path("/api/books/**")
    .filters(f -> f
        .addRequestHeader("X-Gateway", "iron-library")
        .addResponseHeader("X-Response-Time", "#{T(System).currentTimeMillis()}")
        .retry(3))
    .uri("lb://book-service"))
```

## 📚 Documentación API

### Rutas Disponibles
```bash
# Listar todas las rutas configuradas
curl http://localhost:8080/actuator/gateway/routes
```

### Ejemplo de Respuesta
```json
[
  {
    "route_id": "book-service",
    "route_definition": {
      "id": "book-service",
      "predicates": [
        {
          "name": "Path",
          "args": {
            "pattern": "/api/books/**"
          }
        }
      ],
      "uri": "lb://book-service",
      "order": 0
    }
  }
]
```

### Probar Conectividad
```bash
# A través del gateway
curl http://localhost:8080/api/books/health
curl http://localhost:8080/api/users/health  
curl http://localhost:8080/api/loans/health

# Directo a servicios (para comparar)
curl http://localhost:8081/api/books/health
curl http://localhost:8082/api/users/health
curl http://localhost:8083/api/loans/health
```

## 🛠️ Troubleshooting

### Problemas Comunes

**Gateway no encuentra servicios**
```bash
# Verificar que Eureka está corriendo
curl http://localhost:8761/eureka/apps

# Verificar que servicios están registrados
curl http://localhost:8761/eureka/apps/BOOK-SERVICE
```

**Rutas no funcionan**
```bash
# Verificar configuración de rutas
curl http://localhost:8080/actuator/gateway/routes

# Revisar logs del gateway
./mvnw spring-boot:run
```

**Errores 503 Service Unavailable**
- Verificar que los servicios downstream están funcionando
- Confirmar que Eureka puede alcanzar los servicios
- Revisar configuración de timeouts

### Logs Útiles
```properties
# Habilitar logs de debug para gateway
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.reactor.netty=DEBUG
```

## 🚀 Próximas Mejoras

- [ ] **Autenticación** - JWT y OAuth2 integration
- [ ] **Rate Limiting** - Control de velocidad de peticiones
- [ ] **Circuit Breaker** - Resilience4j integration
- [ ] **Request/Response Transformation** - Modificación de payloads
- [ ] **API Versioning** - Soporte para múltiples versiones
- [ ] **Métricas Avanzadas** - Integración con Prometheus
- [ ] **Caching** - Redis integration para respuestas
- [ ] **SSL Termination** - HTTPS en el gateway

## 🔧 Configuración Completa

### application.properties
```properties
# Configuración del gateway
spring.application.name=gateway-service
server.port=8080

# Eureka Service Discovery
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/

# Actuator endpoints
management.endpoints.web.exposure.include=health,gateway,info
management.endpoint.health.show-details=always
```

---

## 📞 Soporte

Para reportar bugs o solicitar nuevas características, crear un issue en el repositorio del proyecto.

**Puerto del servicio**: 8080  
**Nombre en Eureka**: GATEWAY-SERVICE
