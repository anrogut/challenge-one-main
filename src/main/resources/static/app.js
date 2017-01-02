var stompClient = null;
var connected = false;

window.onload = function() {
    setConnected(connected);
}

function setConnected(connected) {
    $('#connect').prop('disabled', connected);
    $('#disconnect').prop('disabled', !connected);
    if (connected) {
        $('#events').show();
    }
    else {
        $('#events').empty();
        $('#files').empty();
    }
}

function connect() {
    $.get('/connect', undefined, function(id) {
        var socket = new SockJS('/ws');

        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            connected = true;
            setConnected(connected);
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/dir/' + id, function (dir) {
                addFileToStructure(dir.body);
            })
            stompClient.subscribe('/topic/event/' + id, function (event) {
                addEvent(event.body);
            });
        });
        setTimeout(function() {
            $.get('/structure');
        },500);
        setTimeout(function() {
            heartbeat();
        }, 30000);
    });
}

function heartbeat() {
    if(connected) {
        $.get('/heartbeat');
        setTimeout(function() {
            heartbeat();
        }, 30000);
    }
}

function addEvent(event) {
    var eventObj = JSON.parse(event);
    if(eventObj.messageType === 'DEFAULT') {
        var labelClass = eventObj.payload.eventType === 'ENTRY_CREATE' ? 'label-success' : 'label-danger';
        $('#events').append('<tr><td><span class="label ' +labelClass + '">' + eventObj.payload.eventType + '</span></td>' +
                            '<td>' + eventObj.payload.absolutePath + '</td></tr>');
    }
}

function addFileToStructure(file) {
    var fileObj = JSON.parse(file);
    if(fileObj.messageType === 'DEFAULT') {
        var humanReadablePath = decodeURIComponent(fileObj.payload.path.substr(8)); // remove 'file:///'
        var iconClass = humanReadablePath.charAt(humanReadablePath.length - 1) === '/' ? 'glyphicon glyphicon-folder-open' : 'glyphicon glyphicon-file';
        $('#files').append('<tr><td><span class="' + iconClass + '"></span>    ' + humanReadablePath + '</td></tr>')
    } else if (fileObj.messageType === 'COMPLETE') {
        $('#alert-container').append('<div class="alert alert-success alert-dismissible success-alert" role="alert">' +
                                    '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
                                        '<strong>' + fileObj.description + '</strong>' +
                                   '</div>')

        setTimeout(function() {
            $(".success-alert").fadeTo(500, 0).slideUp(500, function(){
                $(this).remove();
            });
        }, 5000);
    }
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    connected = false
    setConnected(connected);
    console.log('Disconnected');
}

function addFile() {
    $.get('/addFile?name=' + $('#newFileNameInput').val());
}

$(function () {
    $('form').on('submit', function (e) {
        e.preventDefault();
    });
    $('#connect').click(function() { connect(); });
    $('#disconnect').click(function() { disconnect(); });
    $('#addFile').click(function() { addFile(); });
});