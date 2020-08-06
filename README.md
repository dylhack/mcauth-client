![Build](https://github.com/dhghf/mcauth-client/workflows/Gradle%20Build/badge.svg?branch=production)

# MCAuth Client
This is the Minecraft plugin for [MCAuth](https://github.com/dhghf/mcauth). 
Minecraft server owners must configure and setup their own MCAuth instance
before adding this plugin.


# Setup

## Requirements
JRE 11 or above is required to run this plugin on a Minecraft server.

## 1. Download
Download the latest mcauth-client.jar from [releases](https://github.com/dhghf/mcauth-client/releases/latest)

## 2. Configure
The first time running your server with the plugin in your plugin's folder, it
will generate a new default config. Here is a reference:
```yaml
address: "127.0.0.1" # Where your MCAuth instance is located
port: 3001           # MCAuth instance webserver port (See MCAuth's config.yml)
token: ""            # MCAuth web server token (See MCAuth's config.yml)	
```

## Setup Complete
Your server is now protected, just make sure MCAuth server is running otherwise
players will not be able to join

## Permission Nodes
```
mcauth.addalt
mcauth.remalt
mcauth.getalts
mcauth.listalts
```

# Versioning
`x.y.z`
 - `x` MCAuth version supported
 - `y` A minor change, an important plugin-only change was made
 - `z` A patch, an insignificant change
