<div id="breakpoint-tests">
    <div id="bp-small"></div>
    <div id="bp-medium"></div>
    <div id="bp-large"></div>
    <div id="bp-xlarge"></div>
</div>

<script>
window.DS = window.DS || {};

window.DS.breakpoint = function(size) {
    const breakElement = document.getElementById('bp-' + size);
    return window.getComputedStyle(breakElement, null).display === 'block';
}
</script>
