# JavaFX — macOS Build & Installation Guide

This guide explains how to compile, package, and install the JavaFX application on macOS using Maven, `jpackage`, and `jlink`.

## 1. Prerequisites

- macOS (Apple Silicon or Intel)
- JDK 21 or later, compatible with the project
- Maven Wrapper (`./mvnw`)
- JavaFX and application dependencies configured in `pom.xml`

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
./mvnw -version
```

Verify `jpackage`:

```bash
jpackage --version
```

Ensure `java`, `jlink`, and `jpackage` are available from a compatible JDK installation.

## 2. Configure Maven Dependencies

To automatically copy runtime dependencies during the Maven build, add the following plugin inside `<build><plugins>` in `pom.xml`.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>3.8.1</version>
    <executions>
        <execution>
            <id>copy-runtime-dependencies</id>
            <phase>package</phase>
            <goals>
                <goal>copy-dependencies</goal>
            </goals>
            <configuration>
                <includeScope>runtime</includeScope>
                <outputDirectory>${project.build.directory}/dependency</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This configuration copies required dependency JARs into `target/dependency` during `mvn package`.

## 3. Clean and Build

From the project's root directory:

```bash
./mvnw clean package
```

Expected result:

```text
[INFO] BUILD SUCCESS
```

The build generates:

```text
target/
├── classes/
├── dependency/
│   ├── formsfx-core-*.jar
│   ├── javafx-controls-*.jar
│   └── ...
└── javafx-1.0-SNAPSHOT.jar
```

## 4. Generate macOS Application Image

Use `jpackage` to create a native macOS `.app` bundle.

```bash
jpackage \
  --type app-image \
  --app-version 1.0.0 \
  --name "MyJavaFXApp" \
  --module-path "target/classes:target/dependency" \
  --module com.techbizz.javafx/com.techbizz.javafx.HelloApplication \
  --dest target/image
```

Expected output:

```text
target/image/MyJavaFXApp.app
```

The generated application image includes a Java runtime built by `jlink`, provided all required modules can be resolved and linked.

**Note:** All modules must be compatible with `jlink`. Automatic modules or unsupported native dependencies may require an alternative packaging configuration.

## 5. Test the Application

Launch the generated macOS application:

```bash
open target/image/MyJavaFXApp.app
```

Verify that the JavaFX window opens and the application works correctly.

## 6. Generate the DMG Installer

Create the macOS disk image installer:

```bash
mkdir -p target/installer

jpackage \
  --type dmg \
  --name "MyJavaFXApp" \
  --app-image target/image/MyJavaFXApp.app \
  --dest target/installer
```

Expected output:

```text
target/installer/MyJavaFXApp-1.0.0.dmg
```

The exact filename may vary depending on packaging options and JDK version.

## 7. Open and Install

Open the generated installer:

```bash
open target/installer/*.dmg
```

Then:

1. Open the mounted disk image.
2. Drag `MyJavaFXApp.app` into Applications, if the installer provides that layout.
3. Open Applications.
4. Launch `MyJavaFXApp`.

The packaged application should use its bundled Java runtime rather than requiring a separate system Java installation.

## 8. Complete Build Commands

Run these commands in sequence from the project root.

```bash
# Clean and package the Maven project
./mvnw clean package

# Create the macOS application image
jpackage \
  --type app-image \
  --app-version 1.0.0 \
  --name "MyJavaFXApp" \
  --module-path "target/classes:target/dependency" \
  --module com.techbizz.javafx/com.techbizz.javafx.HelloApplication \
  --dest target/image

# Test the macOS application
open target/image/MyJavaFXApp.app

# Create the DMG installer
mkdir -p target/installer

jpackage \
  --type dmg \
  --name "MyJavaFXApp" \
  --app-image target/image/MyJavaFXApp.app \
  --dest target/installer

# Open the installer
open target/installer/*.dmg
```

## 9. Final Project Structure

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
    ├── javafx-1.0-SNAPSHOT.jar
    ├── image/
    │   └── MyJavaFXApp.app
    └── installer/
        └── MyJavaFXApp-1.0.0.dmg
```

## 10. Troubleshooting

### Invalid application version

Error:

```text
Version [1.0-SNAPSHOT] contains invalid component
```

Solution:

Specify a numeric application version:

```bash
--app-version 1.0.0
```

### JavaFX or FormsFX module not found

Error:

```text
Module com.dlsc.formsfx not found
```

Solution:

Ensure the runtime dependencies are copied:

```bash
./mvnw dependency:copy-dependencies \
  -DincludeScope=runtime \
  -DoutputDirectory=target/dependency
```

Use the complete module path:

```bash
--module-path "target/classes:target/dependency"
```

If the dependency is still missing, inspect the Maven dependency configuration and `module-info.java`.

### Application image already exists

If `jpackage` refuses to overwrite the existing application image:

```bash
rm -rf target/image
```

Then rerun the application image packaging command.

### Automatic modules cannot be linked

If `jlink` reports an error about automatic modules, copying the JARs is not sufficient. Identify the incompatible module and use a supported modular version or an alternative packaging strategy with a separately generated runtime image.

### macOS security warning

Unsigned or unnotarized applications may be blocked by macOS Gatekeeper.

For local development, review the warning and use the macOS security controls to approve the application if you trust the build.

For public distribution, use Apple Developer ID signing and Apple notarization.

### Apple Silicon and Intel compatibility

An application image produced on Apple Silicon generally targets ARM64. An image produced on Intel generally targets x86_64.

Build separate installers for both architectures unless you have specifically configured and tested a compatible universal distribution.

## 11. Distribution

Share the generated file:

```text
target/installer/MyJavaFXApp-1.0.0.dmg
```

Users can install the application by opening the DMG and copying the application into their Applications folder.

For distribution outside a local development environment, sign and notarize the application and installer according to Apple's requirements.

---

**Project:** JavaFX  
**Main module:** `com.techbizz.javafx`  
**Main class:** `com.techbizz.javafx.HelloApplication`  
**Application name:** `MyJavaFXApp`  
**Application version:** `1.0.0`  
**Build tool:** Maven  
**Packaging tools:** `jlink`, `jpackage`  
**Target platform:** macOS
