var exec = require('cordova/exec');

module.exports = {
  start: function(channelId, channelName, notificationId, mediaStreams, selectedIndex) {
    exec(null, null, "MediaStreamingPlugin", "start", [channelId || "playback_channel", channelName || "Playback", notificationId || "1", JSON.stringify(mediaStreams || []), selectedIndex || "0"]);
  },
  play: function(selectedIndex) {
    exec(null, null, "MediaStreamingPlugin", "play", [selectedIndex || "0"]);
  },
  pause: function() {
    exec(null, null, "MediaStreamingPlugin", "pause", []);
  },
  stop: function() {
    exec(null, null, "MediaStreamingPlugin", "stop", []);
  }
};