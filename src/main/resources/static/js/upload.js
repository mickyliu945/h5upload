$(document).ready(function () {
    $("#fileUpload1").fileuploader({
        extensions: ["jpg", "jpeg", "png", "gif", "bmp"],
        changeInput: " ",
        theme: "thumbnails",
        limit:10,
        enableApi: true,
        addMore: true,
        thumbnails: {
            box: '<div class="fileuploader-items">' + '<ul class="fileuploader-items-list">' + '<li class="fileuploader-thumbnails-input"><div class="fileuploader-thumbnails-input-inner">+</div></li>' + "</ul>" + "</div>",
            item: '<li class="fileuploader-item">' + '<div class="fileuploader-item-inner">' + '<div class="thumbnail-holder">${image}</div>' + '<div class="actions-holder">' + '<a class="fileuploader-action fileuploader-action-remove" title="${captions.remove}"><i class="remove"></i></a>' + '<span class="fileuploader-action-popup"></span>' + "</div>" + '<div class="progress-holder">${progressBar}</div>' + "</div>" + "</li>",
            item2: '<li class="fileuploader-item">' + '<div class="fileuploader-item-inner">' + '<div class="thumbnail-holder">${image}</div>' + '<div class="actions-holder">' + '<a class="fileuploader-action fileuploader-action-sort" title="${captions.sort}"><i></i></a>' + '<a class="fileuploader-action fileuploader-action-remove" title="${captions.remove}"><i class="remove"></i></a>' + '<span class="fileuploader-action-popup"></span>' + "</div>" + "</div>" + "</li>",
            startImageRenderer: false,
            canvasImage: false,
            _selectors: {
                list: ".fileuploader-items-list",
                item: ".fileuploader-item",
                start: ".fileuploader-action-start",
                retry: ".fileuploader-action-retry",
                remove: ".fileuploader-action-remove",
                sorter: ".fileuploader-action-sort"
            },
            onItemShow: function (item, listEl) {
                var plusInput = listEl.find(".fileuploader-thumbnails-input");
                plusInput.insertAfter(item.html);
                if (item.format == "image") {
                    item.html.find(".fileuploader-item-icon").hide()
                }
            }
        },
        afterRender: function (listEl, parentEl, newInputEl, inputEl) {
            var plusInput = listEl.find(".fileuploader-thumbnails-input"),
                api = $.fileuploader.getInstance(inputEl.get(0));
            plusInput.on("click", function () {
                api.open()
            })
        },
        upload: {
            url: "./uploadImage",
            data: null,
            type: "POST",
            enctype: "multipart/form-data",
            start: true,
            synchron: true,
            beforeSend: null,
            onSuccess: function (data, item) {
                setTimeout(function () {
                    item.html.find(".fileuploader-action-remove").before('<a class="fileuploader-action fileuploader-action-sort" title="Sort"><i></i></a>');
                    item.html.find(".progress-holder").hide();
                    item.renderThumbnail()
                }, 400)
            },
            onError: function (item) {
                item.html.find(".progress-holder").hide();
                item.html.find(".fileuploader-item-icon i").text("Failed!")
            },
            onProgress: function (data, item) {
                var progressBar = item.html.find(".progress-holder");
                if (progressBar.length > 0) {
                    progressBar.show();
                    progressBar.find(".fileuploader-progressbar .bar").width(data.percentage + "%")
                }
            }
        },
        dragDrop: {container: ".fileuploader-thumbnails-input"},
        onRemove: function (item) {
            $.post("/delete/" + item.name, {file: item.name})
        },
        sorter: {
            selectorExclude: null,
            placeholder: '<li class="fileuploader-item fileuploader-sorter-placeholder"><div class="fileuploader-item-inner"></div></li>',
            scrollContainer: window,
            onSort: function (list, listEl, parentEl, newInputEl, inputEl) {
                var api = $.fileuploader.getInstance(inputEl.get(0)), fileList = api.getFileList(), _list = [];
                $.each(fileList, function (i, item) {
                    _list.push({name: item.name, index: item.index})
                })
            }
        },
    });
    $("#fileUpload2").fileuploader({
        extensions: ["jpg", "jpeg", "png", "gif", "bmp"],
        changeInput: " ",
        theme: "thumbnails",
        limit: 2,
        fileMaxSize: 4,
        enableApi: true,
        addMore: true,
        thumbnails: {
            box: '<div class="fileuploader-items">' + '<ul class="fileuploader-items-list">' + '<li class="fileuploader-thumbnails-input"><div class="fileuploader-thumbnails-input-inner">+</div></li>' + "</ul>" + "</div>",
            item: '<li class="fileuploader-item">' + '<div class="fileuploader-item-inner">' + '<div class="thumbnail-holder">${image}</div>' + '<div class="actions-holder">' + '<a class="fileuploader-action fileuploader-action-remove" title="${captions.remove}"><i class="remove"></i></a>' + '<span class="fileuploader-action-popup"></span>' + "</div>" + '<div class="progress-holder">${progressBar}</div>' + "</div>" + "</li>",
            item2: '<li class="fileuploader-item">' + '<div class="fileuploader-item-inner">' + '<div class="thumbnail-holder">${image}</div>' + '<div class="actions-holder">' + '<a class="fileuploader-action fileuploader-action-sort" title="${captions.sort}"><i></i></a>' + '<a class="fileuploader-action fileuploader-action-remove" title="${captions.remove}"><i class="remove"></i></a>' + '<span class="fileuploader-action-popup"></span>' + "</div>" + "</div>" + "</li>",
            startImageRenderer: false,
            canvasImage: false,
            _selectors: {
                list: ".fileuploader-items-list",
                item: ".fileuploader-item",
                start: ".fileuploader-action-start",
                retry: ".fileuploader-action-retry",
                remove: ".fileuploader-action-remove",
                sorter: ".fileuploader-action-sort"
            },
            onItemShow: function (item, listEl) {
                var plusInput = listEl.find(".fileuploader-thumbnails-input");
                plusInput.insertAfter(item.html);
                if (item.format == "image") {
                    item.html.find(".fileuploader-item-icon").hide()
                }
            }
        },
        afterRender: function (listEl, parentEl, newInputEl, inputEl) {
            var plusInput = listEl.find(".fileuploader-thumbnails-input"),
                api = $.fileuploader.getInstance(inputEl.get(0));
            plusInput.on("click", function () {
                api.open()
            })
        },
        upload: {
            url: "./uploadImage",
            data: null,
            type: "POST",
            enctype: "multipart/form-data",
            start: true,
            synchron: true,
            beforeSend: null,
            onSuccess: function (data, item) {
                item.file = data.src;
                item.choosed = false;
                setTimeout(function () {
                    item.html.find(".fileuploader-action-remove").before('<a class="fileuploader-action fileuploader-action-sort" title="Sort"><i></i></a>');
                    item.html.find(".progress-holder").hide();
                    item.renderThumbnail()
                }, 400)
            },
            onError: function (item) {
                item.html.find(".progress-holder").hide();
                item.html.find(".fileuploader-item-icon i").text("Failed!")
            },
            onProgress: function (data, item) {
                var progressBar = item.html.find(".progress-holder");
                if (progressBar.length > 0) {
                    progressBar.show();
                    progressBar.find(".fileuploader-progressbar .bar").width(data.percentage + "%")
                }
            }
        },
        onRemove: function (item) {
            $.post("/delete?url=" + item.file, {file: item.name})
        },
        sorter: {
            selectorExclude: null,
            placeholder: '<li class="fileuploader-item fileuploader-sorter-placeholder"><div class="fileuploader-item-inner"></div></li>',
            scrollContainer: window,
            onSort: function (list, listEl, parentEl, newInputEl, inputEl) {
                var api = $.fileuploader.getInstance(inputEl.get(0)), fileList = api.getFileList(), _list = [];
                $.each(fileList, function (i, item) {
                    _list.push({name: item.name, index: item.index})
                })
            }
        },
    })
});