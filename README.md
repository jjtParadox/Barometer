# Barometer

An experimental test suite for MinecraftForge mods. Runs with MC 1.12 and requires ForgeGradle.

## Usage

Download the latest release from the GitHub releases page and put it into your project's `./libs` folder.

Add the following to your `build.gradle`'s dependency section:

```gradle
dependencies {
    // ...
    testCompile "org.jetbrains.kotlin:kotlin-stdlib:1.1.2-5"
    testCompile "org.jetbrains.kotlin:kotlin-reflect:1.1.2-5"
    testCompile "junit:junit:4.12"
    testCompile makeStart.outputs.files
}
```

Add the following to the end of `build.gradle`:

```gradle
test {
    // Number of test classes that are run before the server is closed (if this value is wrong things will break!)
    systemProperty 'barometer.numClasses', 1
    
    workingDir = {minecraft.runDir + "/test"} // This can be set to whatever you prefer
    
    mkdir workingDir // Make sure the directory exists.
    
    // NOTE: By using the following statement in your code you are indicating your agreement to the Minecraft EULA (https://account.mojang.com/documents/minecraft_eula).
    file("$workingDir/eula.txt").text = "eula=true" // Automatically agree to the eula
}
```

Make sure the directory `./run/test` (or equivalent) exists.  The gradle test task above will do that 'as is'.

Create the directories `./src/test/java/YourModPackage` for test classes.

Create JUnit tests as usual, except prepend each class with the annotation `@RunWith(BarometerTester.class)`. Look to BarometerExampleTestJava.java for an example.

Run all tests with `gradle test`.

## Notes:
- All tests are server side ONLY.
- Remember to agree to `eula.txt` in your test's run dir.  If you use the gradle test task above, this will be done for you.
- Use `TestUtils.tickServer();` to tick the server while in tests.
- Always update the `barometer.numClasses` property in `build.gradle` every time you create or remove a test class.
- This is very alpha and will probably collapse into a black hole if you sneeze on it. Please report bugs and submit PRs to GitHub!
