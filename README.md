# JMP
Advanced Discord music bot. *why did I even make it*

Made for the boys and for personal use, although you can still use it if you know polish.

Able to play videos from YouTube and locally saved files *(by providing the filepath in `/play`)*.

# Requirements
- Java 17.0.1 LTS 
- **All Privileged Gateway Intents have to be enabled on your Discord Bot Application**

# Usage
In cmd, run this command:
```
java -cp JMP.jar io.github.rudynakodach.Main TOKEN
```
where TOKEN is your bot's token.

## Commands
### Music
- ##### `/copy [pos]`: copies track of index *pos* to the end of the queue
- ##### `/join`: joins the voice channel
- ##### `/jump [position]`: jumps to the track at index *position* removing all tracks behind it from the queue
- ##### `/leave`: leaves the currently connected voice channel
- ##### `/loop`: loops current track
- ##### `/loopqueue`: loops the queue. updates dynamically
- ##### `/np`: shows the currently played track
- ##### `/queue [amt?]`: displays the first *amt* tracks from the queue (5 by default)
- ##### `/rm [pos]`: removes the song at index *pos*
- ##### `/search [query]`: searches for videos on YouTube giving an interface
- ##### `/sp [query]`: like search but instant
- ##### `/seek [t]`: jumps to *t* seconds into the track
- ##### `/skip`: skips the current track
- ##### `/speak`: sends a request to become a speaker in a stage channel
- ##### `/stop`: stops playing and removes the queue
- ##### `/volume [volume]`: changes the volume to *volume* in range 0-1000
### Miscellaneous
- #### `/bugreport`: displays a link to this repo's issues section
- #### `/credits`: sends credits