# PlantUML Image Generator

A Spring Boot REST API service written in Kotlin that generates PlantUML diagrams in various formats (PNG, SVG). The service provides both real-time diagram generation and cached image hosting with automatic cleanup.

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [API Endpoints](#-api-endpoints)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [API Usage Examples](#-api-usage-examples)
- [System Design](#-system-design)

## ✨ Features

- **Multiple Input Formats**: Accept PlantUML source via file upload or JSON request
- **Multiple Output Formats**: Generate diagrams in PNG or SVG format
- **Caching System**: In-memory caching using Caffeine for improved performance
- **Image Hosting**: Generate shareable URLs for cached diagrams
- **Automatic Cleanup**: Scheduled cleanup of stale cached images
- **Access Tracking**: Track image access patterns for intelligent cache management
- **Validation**: Comprehensive input validation for files and requests
- **Error Handling**: Global exception handling with meaningful error messages

## 🛠 Tech Stack

- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.7
- **Java Version**: 21
- **Key Dependencies**:
  - PlantUML 1.2023.9 - Diagram generation engine
  - Caffeine 3.1.8 - High-performance caching library
  - Google Guava 33.5.0 - Hashing utilities
  - Jackson Kotlin Module - JSON serialization
  - Spring AMQP - Message queue support
  - Kotlin Logging - Structured logging

## 🏗 Architecture

### System Architecture Diagram

\`\`\`plantuml
@startuml
!theme plain

package "Client Layer" {
  [Web Client] as Client
  [API Consumer] as Consumer
}

package "Spring Boot Application" {
  package "Controllers" {
    [AppController] as Controller
  }
  
  package "Services" {
    [ImageService] as ImgService
    [PlantUmlService] as PlantService
  }
  
  package "Utilities" {
    [UMLCache] as Cache
    [AccessTracker] as Tracker
    [FileValidator] as Validator
    [CleanupScheduler] as Scheduler
  }
  
  package "Models" {
    [ApiResponse] as Response
    [PlantUMLRequest] as Request
  }
}

package "External" {
  database "Temporary File Storage" as TempFiles
  [PlantUML Library] as PlantUMLLib
}

Client --> Controller : HTTP Requests
Consumer --> Controller : HTTP Requests
Controller --> ImgService : Generate/Retrieve Images
Controller --> PlantService : Direct Generation
ImgService --> Cache : Cache Management
ImgService --> Tracker : Track Access
ImgService --> TempFiles : File I/O
ImgService --> PlantUMLLib : Generate Diagrams
PlantService --> PlantUMLLib : Generate Diagrams
Scheduler --> Tracker : Check Stale Files
Scheduler --> ImgService : Cleanup
Controller ..> Validator : Validate Input
Controller ..> Response : Return Data
Controller ..> Request : Accept Data

@enduml
\`\`\`

### Class Diagram

\`\`\`plantuml
@startuml
!theme plain

class AppController {
  - imageService: ImageService
  + getRoot(): ResponseEntity
  + getHealth(): ResponseEntity
  + postGeneratePlantUML(file, type): ResponseEntity
  + postGeneratePlantUMLText(request): ResponseEntity
  + postGeneratePlantUMLCache(request): ResponseEntity
  + getViewImage(id): ResponseEntity
  - generateUmlResponse(text, type): ResponseEntity
  - createHeaders(outputType): HttpHeaders
}

class ImageService {
  - umlCache: UMLCache
  - accessTracker: AccessTracker
  - tempDir: File
  + generateImage(source): String
  + getImage(id): ByteArray?
  + removeImage(id): void
  + removeAllImages(): void
  - hash(text): String
}

class PlantUmlService {
  + {static} generateImage(text, format): ByteArray
}

class UMLCache {
  - cache: Cache<String, ByteArray>
  + get(id): ByteArray?
  + put(id, data): void
  + evict(id): void
  + removeAll(): void
}

class AccessTracker {
  - lastAccess: ConcurrentHashMap<String, Long>
  + touch(id): void
  + removeOlderThan(cutoff): Set<String>
  + getItemsOlderThan(cutoff): Set<String>
}

class CleanupScheduler {
  - imageService: ImageService
  - accessTracker: AccessTracker
  + cleanup(): void
}

class FileValidator {
  - maxSize: Long
  - allowedTypes: Array<String>
  + isValid(file, context): Boolean
}

class UMLTextValidator {
  + isValid(text, context): Boolean
}

abstract class ApiResponse<T> {
  {abstract} success: Boolean
  {abstract} message: String
  {abstract} data: T?
}

class SuccessResponse<T> {
  + success: Boolean
  + message: String
  + data: T?
}

class ErrorResponse<T> {
  + success: Boolean
  + message: String
  + data: T?
}

class PlantUMLTextRequest {
  + source: String
  + outputType: String
}

class PlantUMLResponse {
  + id: String
  + url: String
}

enum UmlOutputType {
  SVG
  PNG
  + getContentType(): String
}

AppController --> ImageService
AppController --> PlantUmlService
ImageService --> UMLCache
ImageService --> AccessTracker
ImageService --> PlantUmlService
CleanupScheduler --> ImageService
CleanupScheduler --> AccessTracker
AppController ..> FileValidator
AppController ..> UMLTextValidator
ApiResponse <|-- SuccessResponse
ApiResponse <|-- ErrorResponse
AppController ..> PlantUMLTextRequest
AppController ..> PlantUMLResponse
AppController ..> ApiResponse

@enduml
\`\`\`

### Sequence Diagram - Generate with Cache

\`\`\`plantuml
@startuml
!theme plain

actor Client
participant "AppController" as Controller
participant "ImageService" as Service
participant "PlantUmlService" as PlantUML
participant "UMLCache" as Cache
participant "AccessTracker" as Tracker
database "TempFiles" as Files

Client -> Controller: POST /api/plantuml/generate/cache
activate Controller

Controller -> Service: generateImage(source)
activate Service

Service -> Service: hash(source + timestamp)
Service -> Files: check if file exists
alt File exists
  Service -> Cache: put(id, imageBytes)
  Service -> Tracker: touch(id)
  Service --> Controller: return id
else File doesn't exist
  Service -> PlantUML: generateImage(source)
  activate PlantUML
  PlantUML --> Service: ByteArray
  deactivate PlantUML
  
  Service -> Files: writeBytes(imageBytes)
  Service -> Cache: put(id, imageBytes)
  Service -> Tracker: touch(id)
  Service --> Controller: return id
end

deactivate Service

Controller --> Client: PlantUMLResponse(id, url)
deactivate Controller

@enduml
\`\`\`

### Sequence Diagram - Retrieve Cached Image

\`\`\`plantuml
@startuml
!theme plain

actor Client
participant "AppController" as Controller
participant "ImageService" as Service
participant "UMLCache" as Cache
participant "AccessTracker" as Tracker
database "TempFiles" as Files

Client -> Controller: GET /api/plantuml/image/{id}
activate Controller

Controller -> Service: getImage(id)
activate Service

Service -> Cache: get(id)
alt Cache Hit
  Cache --> Service: ByteArray
  Service -> Tracker: touch(id)
  Service --> Controller: ByteArray
else Cache Miss
  Service -> Files: read file
  alt File Exists
    Files --> Service: ByteArray
    Service -> Cache: put(id, ByteArray)
    Service -> Tracker: touch(id)
    Service --> Controller: ByteArray
  else File Not Found
    Service --> Controller: null
  end
end

deactivate Service

alt Image Found
  Controller --> Client: 200 OK (image/png)
else Image Not Found
  Controller --> Client: 404 Not Found
end

deactivate Controller

@enduml
\`\`\`

### Sequence Diagram - Direct Generation

\`\`\`plantuml
@startuml
!theme plain

actor Client
participant "AppController" as Controller
participant "PlantUmlService" as Service
participant "FileValidator" as Validator

Client -> Controller: POST /api/plantuml/file/generate
activate Controller

Controller -> Validator: validate(file)
activate Validator
alt Validation Fails
  Validator --> Controller: ValidationException
  Controller --> Client: 400 Bad Request
else Validation Success
  Validator --> Controller: valid
  deactivate Validator
  
  Controller -> Controller: readInputStreamContent()
  Controller -> Service: generateImage(text, format)
  activate Service
  Service --> Controller: ByteArray
  deactivate Service
  
  Controller -> Controller: createHeaders(outputType)
  Controller --> Client: 200 OK (image bytes)
end

deactivate Controller

@enduml
\`\`\`

### Component Interaction Diagram

\`\`\`plantuml
@startuml
!theme plain

component "Request Layer" {
  [File Upload] as Upload
  [JSON Request] as JSON
}

component "Validation Layer" {
  [FileValidator]
  [UMLTextValidator]
}

component "Controller Layer" {
  [AppController]
}

component "Service Layer" {
  [ImageService]
  [PlantUmlService]
}

component "Cache Layer" {
  [UMLCache]
  [AccessTracker]
}

component "Storage Layer" {
  [Temporary Files]
}

component "Background Jobs" {
  [CleanupScheduler]
}

Upload --> FileValidator
JSON --> UMLTextValidator
FileValidator --> AppController
UMLTextValidator --> AppController
AppController --> ImageService
AppController --> PlantUmlService
ImageService --> UMLCache
ImageService --> AccessTracker
ImageService --> [Temporary Files]
ImageService --> PlantUmlService
CleanupScheduler --> AccessTracker
CleanupScheduler --> ImageService

@enduml
\`\`\`

## 🔌 API Endpoints

### Health & Status

#### GET `/api/` or `/api/`
Health check endpoint.

**Response:**
\`\`\`json
{
  "success": true,
  "message": "PlantUML Hello",
  "data": null
}
\`\`\`

#### GET `/api/health`
Server health status.

**Response:**
\`\`\`json
{
  "success": true,
  "message": "Server is in great condition",
  "data": null
}
\`\`\`

### PlantUML Generation

#### POST `/api/plantuml/file/generate`
Generate diagram from uploaded file.

**Content-Type:** `multipart/form-data`

**Parameters:**
- `file` (required): PlantUML source file
- `outputType` (optional): Output format (`PNG` or `SVG`, default: `PNG`)

**Response:** Binary image data

#### POST `/api/plantuml/json/generate`
Generate diagram from JSON request (direct response).

**Content-Type:** `application/json`

**Request Body:**
\`\`\`json
{
  "source": "@startuml\\nAlice -> Bob: Hello\\n@enduml",
  "outputType": "PNG"
}
\`\`\`

**Response:** Binary image data

#### POST `/api/plantuml/generate/cache`
Generate diagram and return shareable URL.

**Content-Type:** `application/json`

**Request Body:**
\`\`\`json
{
  "source": "@startuml\\nAlice -> Bob: Hello\\n@enduml",
  "outputType": "PNG"
}
\`\`\`

**Response:**
\`\`\`json
{
  "id": "a1b2c3d4e5f6g7h8",
  "url": "@startuml\\nAlice -> Bob: Hello\\n@enduml"
}
\`\`\`

#### GET `/api/plantuml/image/{id}`
Retrieve cached diagram by ID.

**Response:** Binary image data (PNG)

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+

### Installation

1. **Clone the repository**
\`\`\`bash
git clone https://github.com/fiekzz/PlantUML-image-generator.git
cd PlantUML-image-generator
\`\`\`

2. **Build the project**
\`\`\`bash
./mvnw clean install
\`\`\`

3. **Run the application**
\`\`\`bash
./mvnw spring-boot:run
\`\`\`

The server will start on `http://localhost:8080`

### Running with Maven Wrapper (Windows)
\`\`\`cmd
mvnw.cmd spring-boot:run
\`\`\`

## ⚙️ Configuration

### Application Properties

Located in `src/main/resources/application.properties`:

\`\`\`properties
spring.application.name=puml
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n
\`\`\`

### Cache Configuration

**UMLCache** (Caffeine):
- Maximum Size: 10,000 entries
- Expiration: 1 hour after last access

### File Validation

**Allowed File Types:**
- `text/plantuml`
- `application/x-plantuml`
- `text/plain`

**Maximum File Size:** 1 GB

### Cleanup Configuration

**Cleanup Timer:** 15 seconds (configurable in `CleanupScheduler`)

Files older than 15 seconds without access are automatically removed.

## 📝 API Usage Examples

### Using cURL

#### Generate PNG from file
\`\`\`bash
curl -X POST http://localhost:8080/api/plantuml/file/generate \\
  -F "file=@diagram.puml" \\
  -F "outputType=PNG" \\
  --output diagram.png
\`\`\`

#### Generate SVG from JSON
\`\`\`bash
curl -X POST http://localhost:8080/api/plantuml/json/generate \\
  -H "Content-Type: application/json" \\
  -d '{
    "source": "@startuml\\nAlice -> Bob: Authentication Request\\nBob --> Alice: Authentication Response\\n@enduml",
    "outputType": "SVG"
  }' \\
  --output diagram.svg
\`\`\`

#### Generate with caching
\`\`\`bash
curl -X POST http://localhost:8080/api/plantuml/generate/cache \\
  -H "Content-Type: application/json" \\
  -d '{
    "source": "@startuml\\nAlice -> Bob: Hello\\n@enduml",
    "outputType": "PNG"
  }'
\`\`\`

**Response:**
\`\`\`json
{
  "id": "a1b2c3d4e5f6g7h8",
  "url": "@startuml\\nAlice -> Bob: Hello\\n@enduml"
}
\`\`\`

#### Retrieve cached image
\`\`\`bash
curl http://localhost:8080/api/plantuml/image/a1b2c3d4e5f6g7h8 \\
  --output cached-diagram.png
\`\`\`

### Using JavaScript (Fetch API)

\`\`\`javascript
// Generate diagram with caching
async function generateDiagram(plantUmlSource) {
  const response = await fetch('http://localhost:8080/api/plantuml/generate/cache', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      source: plantUmlSource,
      outputType: 'PNG'
    })
  });
  
  const result = await response.json();
  return result.id;
}

// Retrieve the image
async function getImage(imageId) {
  const response = await fetch(\`http://localhost:8080/api/plantuml/image/\${imageId}\`);
  const blob = await response.blob();
  const imageUrl = URL.createObjectURL(blob);
  
  // Display in an img tag
  document.getElementById('diagram').src = imageUrl;
}

// Usage
const plantUml = '@startuml\\nAlice -> Bob: Hello\\n@enduml';
generateDiagram(plantUml).then(id => getImage(id));
\`\`\`

### Using Python

\`\`\`python
import requests

# Generate diagram directly
def generate_diagram(source, output_type='PNG'):
    url = 'http://localhost:8080/api/plantuml/json/generate'
    response = requests.post(url, json={
        'source': source,
        'outputType': output_type
    })
    
    if response.status_code == 200:
        with open('diagram.png', 'wb') as f:
            f.write(response.content)
        return True
    return False

# Generate with caching
def generate_cached(source):
    url = 'http://localhost:8080/api/plantuml/generate/cache'
    response = requests.post(url, json={
        'source': source,
        'outputType': 'PNG'
    })
    return response.json()['id']

# Retrieve cached image
def get_cached_image(image_id):
    url = f'http://localhost:8080/api/plantuml/image/{image_id}'
    response = requests.get(url)
    
    if response.status_code == 200:
        with open(f'{image_id}.png', 'wb') as f:
            f.write(response.content)
        return True
    return False

# Usage
plantuml_source = """
@startuml
Alice -> Bob: Authentication Request
Bob --> Alice: Authentication Response
@enduml
"""

# Direct generation
generate_diagram(plantuml_source)

# Or with caching
image_id = generate_cached(plantuml_source)
get_cached_image(image_id)
\`\`\`

## 🎯 System Design

### Caching Strategy

The application uses a two-tier caching approach:

1. **In-Memory Cache (Caffeine)**: Fast access to recently generated diagrams
2. **File System Cache**: Persistent storage for diagram files

### Cache Flow Diagram

\`\`\`plantuml
@startuml
!theme plain

start

:Request Image by ID;

if (Check Memory Cache) then (Hit)
  :Return from Cache;
  :Update Access Tracker;
  stop
else (Miss)
  if (Check File System) then (Exists)
    :Read from File;
    :Store in Cache;
    :Update Access Tracker;
    :Return Image;
    stop
  else (Not Found)
    :Return 404;
    stop
  endif
endif

@enduml
\`\`\`

### Cleanup Process

\`\`\`plantuml
@startuml
!theme plain

start

:CleanupScheduler Runs;

:Get Current Timestamp;
:Calculate Cutoff Time\\n(Current - 15s);

:Query AccessTracker\\nfor Stale IDs;

if (Stale IDs Found?) then (yes)
  :Iterate Stale IDs;
  repeat
    :Remove from File System;
    :Evict from Cache;
    :Remove from Tracker;
  repeat while (More IDs?)
else (no)
  :No Action;
endif

:Schedule Next Run;

stop

@enduml
\`\`\`

### Error Handling Flow

\`\`\`plantuml
@startuml
!theme plain

start

:Incoming Request;

if (Validation Pass?) then (yes)
  if (Processing Success?) then (yes)
    :Return 200 OK;
    stop
  else (no)
    :Internal Error;
    :GlobalExceptionHandler;
    :Return 500 Error;
    stop
  endif
else (no)
  if (File Size Exceeded?) then (yes)
    :Return 413\\nPayload Too Large;
    stop
  else (no)
    if (Invalid File Type?) then (yes)
      :Return 400\\nBad Request;
      stop
    else (no)
      if (Invalid Field?) then (yes)
        :Return 400\\nValidation Failed;
        stop
      endif
    endif
  endif
endif

@enduml
\`\`\`

## 🔐 Security Considerations

- File size validation prevents DOS attacks
- Content-type validation prevents malicious file uploads
- Automatic cleanup prevents disk space exhaustion
- Input sanitization through validation annotations

## 📊 Performance Optimizations

1. **Caffeine Cache**: High-performance in-memory caching
2. **Concurrent Hash Map**: Thread-safe access tracking
3. **Lazy File Generation**: Files generated only when needed
4. **Background Cleanup**: Non-blocking cleanup process

## 🐛 Error Responses

### Validation Error (400)
\`\`\`json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "file": "Invalid file type. Allowed: text/plantuml, application/x-plantuml, text/plain"
  }
}
\`\`\`

### File Too Large (413)
\`\`\`json
{
  "success": false,
  "message": "File size exceeded maximum allowed size",
  "data": null
}
\`\`\`

### Not Found (404)
\`\`\`json
{
  "success": false,
  "message": "Image not found",
  "data": null
}
\`\`\`

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is open source and available under the MIT License.

## 👤 Author

**fiekzz**
- GitHub: [@fiekzz](https://github.com/fiekzz)
- Repository: [PlantUML-image-generator](https://github.com/fiekzz/PlantUML-image-generator)

## 🙏 Acknowledgments

- [PlantUML](https://plantuml.com/) - The diagram generation engine
- [Spring Boot](https://spring.io/projects/spring-boot) - The application framework
- [Caffeine](https://github.com/ben-manes/caffeine) - High-performance caching library
- [Kotlin](https://kotlinlang.org/) - The programming language

---

**Note**: This service is designed for development and demonstration purposes. For production use, consider adding:
- Database persistence
- Authentication/Authorization
- Rate limiting
- Load balancing
- CDN integration for image delivery
- Monitoring and alerting