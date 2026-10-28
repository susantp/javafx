# JavaFX — macOS Build & Installation Guide

This guide describes how to build, package, and install the JavaFX application on macOS using Maven and the JDK packaging tools.

The build process generates a native macOS `.app` bundle and a distributable `.dmg` installer.

## 1. Prerequisites

Ensure the following tools are installed:

- macOS (Apple Silicon or Intel)
- JDK 21 or later
- Maven Wrapper (`./mvnw`)
- JavaFX dependencies configured in `pom.xml`

Verify the environment:

```bash
java -version
./mvnw -version
jlink --version
jpackage --version
```

## 2. Build the Application

From the project root, run:

```bash
./mvnw clean package dependency:copy-dependencies \
  -DincludeScope=runtime \
  -DoutputDirectory=target/dependency
```

This compiles the application, runs tests, generates the application JAR, and copies runtime dependencies.

Expected output:

```text
[INFO] BUILD SUCCESS
```

Build artifacts are generated under:

```text
target/
├── classes/
├── dependency/
│   └── *.jar
└── javafx-1.0-SNAPSHOT.jar
```

## 3. Create the macOS Application

Generate the native `.app` bundle:

```bash
jpackage \
  --type app-image \
  --app-version 1.0.0 \
  --name "MyJavaFXApp" \
  --module-path "target/classes:target/dependency" \
  --module com.techbizz.javafx/com.techbizz.javafx.HelloApplication \
  --dest target/image
```

Generated application:

```text
target/image/MyJavaFXApp.app
```

The application bundle includes its own Java runtime and required application modules.

**Note:** This command requires dependencies to be compatible with the Java module system and runtime linking.

## 4. Test the Application

Launch the generated application:

```bash
open target/image/MyJavaFXApp.app
```

Verify that the application starts and operates correctly before creating the installer.

## 5. Create the DMG Installer

Package the application into a macOS disk image:

```bash
jpackage \
  --type dmg \
  --name "MyJavaFXApp" \
  --app-image target/image/MyJavaFXApp.app \
  --dest target/installer
```

Generated installer:

```text
target/installer/MyJavaFXApp-1.0.0.dmg
```

The exact installer filename depends on the packaging configuration.

## 6. Install the Application

Open the DMG:

```bash
open target/installer/*.dmg
```

Installation steps:

1. Open the mounted disk image.
2. Copy `MyJavaFXApp.app` into the Applications folder.
3. Open Applications in Finder.
4. Launch `MyJavaFXApp`.

The application runs using its bundled Java runtime. A separate JDK installation is not required on the target machine.

## 7. Complete Build Commands

Run the following commands from the project root:

```bash
# Build application and dependencies
./mvnw clean package dependency:copy-dependencies \
  -DincludeScope=runtime \
  -DoutputDirectory=target/dependency

# Generate macOS application bundle
jpackage \
  --type app-image \
  --app-version 1.0.0 \
  --name "MyJavaFXApp" \
  --module-path "target/classes:target/dependency" \
  --module com.techbizz.javafx/com.techbizz.javafx.HelloApplication \
  --dest target/image

# Launch application
open target/image/MyJavaFXApp.app

# Generate DMG installer
jpackage \
  --type dmg \
  --name "MyJavaFXApp" \
  --app-image target/image/MyJavaFXApp.app \
  --dest target/installer

# Open installer
open target/installer/*.dmg
```

## 8. Build Output Structure

```text
javafx/
├── pom.xml
├── mvnw
├── INSTALL_MAC.md
├── src/
│   └── main/
│       ├── java/
│       └── resources/
└── target/
    ├── classes/
    ├── dependency/
    │   └── *.jar
    ├── javafx-1.0-SNAPSHOT.jar
    ├── image/
    │   └── MyJavaFXApp.app
    └── installer/
        └── MyJavaFXApp-1.0.0.dmg
```

## 9. Troubleshooting

### Invalid Application Version

**Error:**

```text
Version [1.0-SNAPSHOT] contains invalid component
```

Use a numeric application version:

```bash
--app-version 1.0.0
```

### Module Not Found

**Example:**

```text
Module com.dlsc.formsfx not found
```

Verify that dependencies exist:

```bash
ls target/dependency
```

Copy runtime dependencies again if required:

```bash
./mvnw dependency:copy-dependencies \
  -DincludeScope=runtime \
  -DoutputDirectory=target/dependency
```

Verify the module path:

```bash
--module-path "target/classes:target/dependency"
```

### Automatic Module Errors

Runtime linking may fail when third-party dependencies are not compatible with `jlink`.

Use compatible modular dependencies or generate a custom runtime image and package the application using `jpackage` with the appropriate runtime and application classpath.

### Application Image Already Exists

Remove the existing application image:

```bash
rm -rf target/image
```

Run the application packaging command again.

### macOS Security Warnings

macOS may block unsigned applications through Gatekeeper.

For local builds, follow the macOS security prompts to authorize trusted applications.

For public distribution, sign the application using an Apple Developer ID certificate and notarize the distributable package.

### Apple Silicon and Intel Compatibility

Create separate builds for each macOS architecture:

- **Apple Silicon:** ARM64 (aarch64)
- **Intel:** x86_64

Build and verify the application on the corresponding target architecture.

## 10. Distribution

Distribute the generated installer:

```text
target/installer/MyJavaFXApp-1.0.0.dmg
```

Users can install the application by opening the DMG and copying the `.app` bundle into Applications.

## 11. README Integration

Add the installation guide to `README.md`:

```markdown
## Installation

- [macOS Installation Guide](INSTALL_MAC.md)
```

---

**Application:** MyJavaFXApp  
**Main module:** `com.techbizz.javafx`  
**Main class:** `com.techbizz.javafx.HelloApplication`  
**Version:** `1.0.0`  
**Build tool:** Maven  
**Packaging tools:** jlink, jpackage  
**Platform:** macOS
