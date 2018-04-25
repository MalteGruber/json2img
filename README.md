
# json2img 
json2img is an image generator for video creation that was part of the
backend for a website. This website is no longer active but the code 
and concept are made public here in the hope that it will be useful.

## Why
json2img converter is a tool in a chain of tools that can be used to create videos of drawn text from user input drawn in a browser. The user
input is streamed from the website to the backend as json encoded events
such as "move","press","release","tool", etc. This approach allows for
the creation of video tutorials and presentations directly in a 
web-browser, without the need of video capture software which can be problematic to run on slower devices. Further, the video will not have any artifacts from the creator's computer such as mouse cursors, 
popups, etc. The output video could, in theory, have infinitely high resolution without affecting the creator's device.

## Input

Mouse position event
**{x:0,y:0,t:14204212}**

Mouse click event 1 is down and 2 is up
**{mouse:2,x:23,y:42,t:14204212}**

Tool selection
**{tool:0,t:14204212}**

Color selection, RGB string with either three or six characters
**{color:"#930",t:14204212}**
**{color:"#9a3b0c",t:14204212}**

Thikness event
**{thk:30,t:14204212}**

## Output
Output images can either be written to a folder or they can be sent to the standard output.

## Creating A Video from the Images
Using **ffmpeg** the output of **json2img** can be converted to a video.
This can be done while json2img is running with pipes.

**json2img** can be configured to only send the images to its standard output
together with ffmped a video can be created in the following way

     java -jar json2img.jar | ffmpeg -framerate 60 -i - -c:v libx264 //
     -profile:v high -crf 20 -pix_fmt yuv420p output.mp4

The `-i -` flag tells **ffmpeg** that the input is to be taken from its standard input.
