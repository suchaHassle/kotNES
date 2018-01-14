# kotNES

A Nintendo Entertain System Emulator written in Kotlin.

## Running

Load an iNES ROM using File - Open. The should start immediately unless there is an error otherwise. If you wish to switch renderers, right click and select your available renderers.

## Controls

Controls are hard coded at the moment.

* A/B - X/Z respectively
* Start - Enter
* Select - S
* Up/Down/Left/Right - Arrow Keys

## Performance and Compatibility

Special thanks to [@Xyene](https://github.com/Xyene) for lending me his Nitrous renderers. For extra rendering speed, kotNES bypasses the Java2D API to obtain rendering contexts for the various pipelines Java2D supports simultaneously. Current supported renders: OpenGL, Direct3D, GDI, and X. Tested for Ubuntu 16.10+, Windows 8, and Windows 10.

## Title Screens

Since title screens are nice:

![](https://i.imgur.com/fKE5pDA.png)
![](https://i.imgur.com/764ZX8V.png)
![](https://i.imgur.com/j8IR6hI.png)


## License

[MIT](https://github.com/suchaHassle/kotNES/blob/master/LICENSE) © Jason Pham