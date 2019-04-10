/* jshint ignore:start */
export default function (name, $window) {
    var win = $window || window;
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regexS = "[\\?&]" + name + "=([^&#]*)";
    var regex = new RegExp(regexS);
    var results = regex.exec(win.location.href);
    if (results === null) {
        return null;
    } else {
        return results[1];
    }
}
/* jshint ignore:end */
