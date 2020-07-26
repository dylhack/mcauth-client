# MCAuth Client
This is the Minecraft plugin for [MCAuth](https://github.com/dhghf/mcauth). 
Minecraft server owners must configure and setup their own MCAuth instance
before adding this plugin.

## Requirements
 * JRE 11+ (for running)
 * JDK 11+ (for compiling)
 * [Gradle 6.4+](https://gradle.org/) (for compiling)

# Setup

## 1. Building
```
$ gradle build
```

## 2. Add to Your Server
The build output is located in `build/lib/mcauth-client-2.0.0-all.jar` copy it
to your server plugin's folder.

## 3. Configure
The first time running your server with the plugin in your plugin's folder, it
will generate a new default config. Here is a reference:
```yaml
address: "127.0.0.1" # Where your MCAuth instance is located
port: 3001 			 # MCAuth instance webserver port (See MCAuth's config.yml)
token: ""			 # MCAuth web server token (See MCAuth's config.yml)	
kick_delay: 6		 # Kick delay in seconds
```
