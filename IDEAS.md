# INFO
This is where I store my ideas for next features

## ParticleText
The name practically explains itself. I would want to implement an algorithm which takes a String and outputs a List of Locations for Particles to be placed

## Building visualization
Takes in 2 locations and loops every block inside that selection. Algorithm then decides which blocks are outline of the building and returns List of Locations, or it may instantly visualize them

## Module settings
Settings class
- Using reflection to get declaredFields
- Saving declaredFields to json, then on server start changing declared fields to those saved in json

For those things i would need to implement
- Json handler ? I don't know if there is one already in java
- Some settings handler, handling all the modules settings (Could be in AModuleHandler or in FamiModuleHandler)

