A [Giter8][g8] template for creating Scala Play projects for HMRC digital services

**To use the template you need to have SBT version of 0.13.13 or later**<br>
Information to install this on linux can be found here: <br>
http://www.scala-sbt.org/0.13/docs/Installing-sbt-on-Linux.html

To create a template service:
1. Locate to the directory where you want to create the template
2. To create a generic microservice run the command <br>
`sbt new nkaraolis/hmrc-service-template.g8` <br>
If you want to create a frontend run the command <br>
`sbt new nkaraolis/hmrc-service-template.g8 -b Frontend` <br>
Or if you want to create a third party API run the command <br>
`sbt new nkaraolis/hmrc-service-template.g8 -b API`
3. Enter the name of your new service when prompted and then the new project will be created

Template license
----------------
Written by Nick Karaolis

To the extent possible under law, the author(s) have dedicated all copyright and related
and neighboring rights to this template to the public domain worldwide.
This template is distributed without any warranty. See <http://creativecommons.org/publicdomain/zero/1.0/>.

[g8]: http://www.foundweekends.org/giter8/
