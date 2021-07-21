# gradle plugin `ru.tutu.dependencies2files`
Helps to save dependencies in files, and use them offline later.  
Main goal to save unique jcenter() dependencies to latter usage.

### 1. Configure plugin
#### Usage in build.gradle
```Groovy
plugins {
  id("ru.tutu.dependencies2files") version "0.1.0"
}
//...
repositories {
  mavenCentral()
  // add proxy repository to save unique jcenter() dependencies:
  dependenciesToFiles.addRepository(it) {
    saveJcenter()
  }
}
```

#### Usage in build.gradle.kts
```Kotlin
plugins {
  id("ru.tutu.dependencies2files") version "0.1.0"
}
//...
repositories {
  mavenCentral()
  // add proxy repository to save unique jcenter() dependencies:
  dependenciesToFiles.addRepository(this) {
    saveJcenter()
  }
}
```
### 2. Build your project
Compile or build your project, sync IDE with Gradle. Use all cases you want to work with desired dependencies. Also, it'is better use gradle cli option `--refresh-dependencies`.
### 3. Commit to git
Better to commit all saved dependencies to git. By default all dependencies will saved to `save_dependencies` dir.
### 4. Remove plugin and use mavenLocal instead 
Instead `dependenciesToFiles.addRepository(...)` you may use mavenLocal with custom dir:
```Kotlin
repositories {
  mavenCentral()
  mavenLocal {
    url = uri("${rootProject.projectDir}/save_dependencies")
  }
}
```
### 5. Check how it works without plugin
Now you may check how saved dependencies will work with your build. Sync IDE, compile your project. Good luck in future!

## Advanced usage
Also you may configure plugin in extension:
```Kotlin
dependenciesToFiles {
  saveDependenciesDir = "another_directory"
  offlineMode = false // you may check dependencies availability with offline toggle
}
```

