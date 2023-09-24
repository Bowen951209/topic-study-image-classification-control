# Topic Study
### Supported platform:
* **Windows x64**

Since this project only include OpenCV windows x64 java library build, it only supports window x64.

### How to build and run:
You need to download the necessary files and put to **"resources/externalFiles/"** or else some program won't work.
|File|Download Link|
| ------------ | ------------ |
|yolov3.weights|https://pjreddie.com/media/files/yolov3.weights|



Build and run is written in the same batch file. Just simply run **buildAndRun.bat**.
To change the main class, modify below lines in **buildAndRun.bat**:

	set APP_STARTER=NameOfTheMainClass
	set APP_STARTER_PACKAGE=package.of.the.main.class
