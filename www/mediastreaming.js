var exec = require('cordova/exec');

module.exports = {
  start: function(options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MediaStreamingPlugin", "start", [options.channelId || "playback_channel", options.channelName || "Playback", options.notificationId || "1", JSON.stringify(options.mediaStreams || []), options.selectedIndex || "0"]);
  },
  play: function(options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MediaStreamingPlugin", "play", [options.channelId || "playback_channel", options.channelName || "Playback", options.notificationId || "1", JSON.stringify(options.mediaStreams || []), options.selectedIndex || "0"]);
  },
  pause: function(options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MediaStreamingPlugin", "pause", [options.channelId || "playback_channel", options.channelName || "Playback", options.notificationId || "1", JSON.stringify(options.mediaStreams || []), options.selectedIndex || "0"]);
  },
  stop: function(options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MediaStreamingPlugin", "stop", [options.channelId || "playback_channel", options.channelName || "Playback", options.notificationId || "1", JSON.stringify(options.mediaStreams || []), options.selectedIndex || "0"]);
  },
  close: function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "MediaStreamingPlugin", "close", ["playback_channel", "Playback", "1", JSON.stringify([]), "0"]);
  }
};