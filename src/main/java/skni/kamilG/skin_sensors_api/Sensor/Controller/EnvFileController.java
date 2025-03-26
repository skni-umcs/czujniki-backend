package skni.kamilG.skin_sensors_api.Sensor.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnvFileController {

  @GetMapping(value = "/api/v1/.env", produces = "text/plain")
  public ResponseEntity<String> serveEnvFile() {
    String fakeEnv =
        """
                # Environment Configuration
                NODE_ENV=production
                PORT=8080

                # Database Configuration
                DB_CONNECTION=postgres
                DB_HOST=db.skni.lol
                DB_PORT=5432
                DB_DATABASE=app_production
                DB_USERNAME=app_user
                DB_PASSWORD=jGt5Kl7NpQ2Ws3

                # Redis Cache
                REDIS_HOST=cache.skni.lol
                REDIS_PORT=6379
                REDIS_PASSWORD=pRs8Mn6vXz1

                # API Configuration
                API_TIMEOUT=30000
                API_RATE_LIMIT=100

                # JWT Authentication
                JWT_SECRET=c29a5787cwel884e3z5a2ciebie4ded604cd50935
                JWT_EXPIRES=86400

                # External Services
                STRIPE_KEY=sk_test_51ABCDEFGhijklMNOPqrsTUVwxyz0123456789
                MAILGUN_DOMAIN=mg.skni.lol
                MAILGUN_SECRET=key-3ax6xnjp29jd6fds4gc373sgvjxteol0

                # Monitoring
                SENTRY_DSN=https://1a2b3c4d5e6f7g8h9i0j@o123456.ingest.sentry.io/7654321

                # Feature Flags
                ENABLE_NEW_DASHBOARD=true
                ENABLE_ANALYTICS=true
                """;

    return ResponseEntity.ok(fakeEnv);
  }

  @GetMapping(value = "/api-docs", produces = "application/json")
  public ResponseEntity<String> serveApiDocs() {
    String fakeApiDocs =
        """
                {
                  "openapi": "3.0.1",
                  "info": {
                    "title": "Internal API Documentation",
                    "description": "API for internal services only",
                    "version": "1.2.0",
                    "contact": {
                      "email": "dev@example.com"
                    }
                  },
                  "servers": [
                    {
                      "url": "https://api.example.com/v1"
                    },
                    {
                      "url": "https://staging-api.example.com/v1"
                    }
                  ],
                  "paths": {
                    "/users": {
                      "get": {
                        "summary": "Get users list",
                        "security": [{"BearerAuth": []}],
                        "parameters": [
                          {
                            "name": "page",
                            "in": "query",
                            "schema": {"type": "integer"}
                          }
                        ],
                        "responses": {
                          "200": {
                            "description": "List of users"
                          }
                        }
                      }
                    },
                    "/auth/login": {
                      "post": {
                        "summary": "User login",
                        "requestBody": {
                          "content": {
                            "application/json": {
                              "schema": {
                                "properties": {
                                  "email": {"type": "string"},
                                  "password": {"type": "string"}
                                }
                              }
                            }
                          }
                        },
                        "responses": {
                          "200": {
                            "description": "Login successful"
                          }
                        }
                      }
                    }
                  },
                  "components": {
                    "securitySchemes": {
                      "BearerAuth": {
                        "type": "http",
                        "scheme": "bearer"
                      }
                    }
                  }
                }
                """;

    return ResponseEntity.ok(fakeApiDocs);
  }
}
