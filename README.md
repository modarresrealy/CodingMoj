# Bouncing Cube Android App

A lightweight Android application that displays a colorful 3D cube spinning and bouncing against the edges of the screen on a white background.

## Features

- **3D Rendered Cube**: Uses OpenGL ES 2.0 for hardware-accelerated rendering
- **Spinning Animation**: Continuous rotation on multiple axes for visual appeal
- **Physics Simulation**: Realistic bouncing with velocity damping
- **Colorful Design**: Each face of the cube has different colors (red, green, blue, yellow, magenta, cyan, brown, gray)
- **Responsive**: Adapts to different screen sizes and orientations
- **Lightweight**: Minimal dependencies and optimized performance

## Technical Details

- **Minimum SDK**: Android 5.0 (API level 21)
- **Target SDK**: Android 14 (API level 34)
- **Graphics**: OpenGL ES 2.0
- **Architecture**: Single activity with OpenGL surface view
- **Physics**: Custom bouncing algorithm with screen edge detection

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/bouncingcube/
│   │   ├── MainActivity.java          # Main activity with OpenGL setup
│   │   └── CubeRenderer.java         # OpenGL renderer with cube geometry and physics
│   ├── res/
│   │   └── layout/
│   │       └── activity_main.xml     # Basic layout (OpenGL view added programmatically)
│   └── AndroidManifest.xml           # App configuration with OpenGL ES requirement
├── build.gradle                      # App-level dependencies and configuration
└── proguard-rules.pro                # ProGuard rules for release builds
```

## Building the App

### Prerequisites

- Android Studio 2022.3.1 or later
- Android SDK with API level 34
- Java 8 or later

### Build Instructions

1. **Clone/Download**: Ensure you have all project files in your workspace

2. **Open in Android Studio**: 
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory

3. **Sync Project**: 
   - Android Studio will automatically prompt to sync Gradle
   - Click "Sync Now" if prompted

4. **Build APK**:
   ```bash
   ./gradlew assembleDebug
   ```

5. **Install to Device**:
   ```bash
   ./gradlew installDebug
   ```

### Alternative Build (Command Line)

If you prefer command-line building:

```bash
# Make gradlew executable (if not already)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# The APK will be generated at:
# app/build/outputs/apk/debug/app-debug.apk
```

## Running the App

1. **On Physical Device**:
   - Enable Developer Options and USB Debugging
   - Connect device via USB
   - Run `./gradlew installDebug` or use Android Studio's Run button

2. **On Emulator**:
   - Create an Android Virtual Device (AVD) with API level 21+
   - Ensure hardware acceleration is enabled
   - Run the app from Android Studio

## How It Works

### Cube Geometry
The cube is defined with 8 vertices forming a 3D cube, with each vertex having a unique color. The cube is rendered using indexed drawing with triangles.

### Animation System
- **Rotation**: Continuous rotation around X, Y, and Z axes
- **Translation**: Position updates based on velocity vectors
- **Physics**: Collision detection with screen boundaries triggers velocity reversal

### Rendering Pipeline
1. **Vertex Shader**: Transforms 3D coordinates and passes colors
2. **Fragment Shader**: Applies interpolated colors to pixels
3. **MVP Matrix**: Model-View-Projection transformation for 3D rendering
4. **Continuous Rendering**: 60 FPS rendering loop for smooth animation

## Customization

### Modify Cube Colors
Edit the `colors` array in `CubeRenderer.java`:
```java
private static final float[] colors = {
    0.8f, 0.2f, 0.2f, 1.0f, // Red
    0.2f, 0.8f, 0.2f, 1.0f, // Green
    // ... add your colors
};
```

### Adjust Physics
Modify these constants in `CubeRenderer.java`:
```java
private float velocityX = 0.02f;     // Horizontal speed
private float velocityY = 0.015f;    // Vertical speed
private static final float BOUNCE_DAMPING = 0.98f; // Energy loss on bounce
```

### Change Background Color
Update the clear color in `onSurfaceCreated()`:
```java
GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // White background
```

## Performance Notes

- Uses hardware-accelerated OpenGL ES 2.0
- Optimized vertex buffers for efficient rendering
- Minimal memory allocations during runtime
- Suitable for devices with limited GPU capabilities

## License

This project is created as a demonstration and is free to use and modify.
