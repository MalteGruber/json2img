# json2img 
json2img is an image generator for video creation that was part of the
backend for a website. This website is no longer active but the code 
and concept are made public here in the hope that it will be useful.

## Why
json2img converter is a tool in a chain of tools that can be used to 
create videos of drawn text from user input drawn in a browser. The user
input is streamed from the website to the backend as json encoded events
such as "move","press","release","tool", etc. This approach allows for
the creation of video tutorials and presentations directly in a 
web-browser, without the need of video capture software which can be 
problematic to run on slower devices. Further, the video will not have 
any artifacts from the creator's computer such as mouse cursors, 
popups, etc. The output video could, in theory, have infinitely high 
resolution without affecting the creator's device.
