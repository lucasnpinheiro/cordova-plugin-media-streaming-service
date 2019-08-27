var exec = require('cordova/exec');

module.exports = {
  start: function(options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MediaStreamingPlugin", "start", [options.channelId || "playback_channel", options.channelName || "Playback", options.notificationId || "1", JSON.stringify(options.mediaStreams || []), options.selectedIndex || "0"]);
  },
  play: function(selectedIndex, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MediaStreamingPlugin", "play", [selectedIndex || "0"]);
  },
  pause: function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MediaStreamingPlugin", "pause", []);
  },
  stop: function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MediaStreamingPlugin", "stop", []);
  }
};