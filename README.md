# Barometer

 [ ![Latest](https://api.bintray.com/packages/jjtparadox/MC/Barometer/images/download.svg) ](https://bintray.com/jjtparadox/MC/Barometer/_latestVersion)

An experimental test suite for MinecraftForge mods. Runs with MC 1.12 and requires ForgeGradle.

## Usage

Add the following to your `build.gradle` in the appropriate sections:

```gradle
buildscript {
    repositories {
        // ...
        // Forge's stuff here...
        // ...
        maven { url "https://dl.bintray.com/jjtparadox/MC" }
    }
    dependencies {
        // More Forge stuff
        // ...
        classpath 'com.jjtparadox.barometer:Barometer:+' // Use latest version above
    }
}

// Plugins, including 'net.minecraftforge.gradle.forge'
// ...
apply plugin: 'com.jjtparadox.barometer' // Must be after the Forge plugin

repositories {
    // Other repositories
    // ...
    maven {
        url "https://dl.bintray.com/jjtparadox/MC"
    }
}

dependencies {
    // Other dependencies
    // ...
   testCompile 'com.jjtparadox.barometer:Barometer:+' // Use latest version above
}

```

Add the following to the end of `build.gradle`:

```gradle
barometer {
    testServerDir = 'testing' // Optional. Defaults to "runDir/test"
    
    // NOTE: By using the following statement in your code you are indicating your agreement to the Minecraft EULA (https://account.mojang.com/documents/minecraft_eula).
    acceptEula()
}
```

Create the directories `./src/test/java/YourModPackage` for test classes.

Create JUnit tests as usual, except prepend each class with the annotation `@RunWith(BarometerTester.class)`. Look to BarometerExampleTestJava.java for an example.

Run all tests with `gradle test`.

## Notes:
- All tests are server side ONLY.
- Use `TestUtils.tickServer();` to tick the server while in tests.
- This is very alpha and will probably collapse into a black hole if you sneeze on it. Please report bugs and submit PRs to GitHub!
