<%@ include file="/WEB-INF/jsp/include.jsp"%>
<script type="text/javascript" src="http://extjs-public.googlecode.com/svn/tags/extjs-4.2.1/release/ext-all-dev.js"></script>
<script type="text/javascript" src="http://extjs-public.googlecode.com/svn/tags/extjs-4.2.1/release/packages/ext-theme-neptune/build/ext-theme-neptune.js"></script>
<link rel="stylesheet" type="text/css" href="/wasp/css/ext-theme-neptune-all-wasp.css" />

<style type="text/css">
    .task {
        background-image: url(/wasp/css/ext/images/icons/fam/cog.gif) !important;
    }
    .task-folder {
        background-image: url(/wasp/css/ext/images/icons/fam/folder_go.gif) !important;
    }
</style>


<script type="text/javascript">

Ext.require([
    'Ext.data.*',
    'Ext.tree.*',
    'Ext.util.*',
    'Ext.toolbar.Paging'
]);

// we want to setup a data model and store instead of using dataUrl this is mirrored in BatchJobTreeModel.java
// such that when a list of BatchJobTreeModel data is returned as Json, it maps directly to the BatchTreeModel model class defined below
Ext.define('BatchTreeModel', {
    extend: 'Ext.data.TreeModel',
    fields: [
        {name: 'name',     type: 'string'},
        {name: 'executionId',     type: 'long'},
        {name: 'startTime', type: 'string'},
        {name: 'endTime',     type: 'string'},
        {name: 'status',     type: 'string'},
        {name: 'exitCode', type: 'string'},
        {name: 'exitMessage',     type: 'string'}
    ]
});

// TreeStore is not directly designed for paging yet but this implementation of a TreeGridStore works nicely
// http://www.sencha.com/forum/showthread.php?205428-Extjs-tree-grid-paging-toolbar-with-remote-store&p=888643&viewfull=1#post888643
Ext.define("TREEGRIDS.store.TreeGridStore", {    extend : "Ext.data.TreeStore",
    getTotalCount : function() {
        if(!this.proxy.reader.rawData) return 0;
        this.totalCount = this.proxy.reader.rawData.totalCount;
        return this.totalCount;
    },
    getStore : function() {
        return this;
    },
    currentPage : 1,
    clearRemovedOnLoad: true,
    clearOnPageLoad : true,
    addRecordsOptions: {
        addRecords: true
    },
    loadPage: function(page, options) {
        var me = this;


        me.currentPage = page;


        // Copy options into a new object so as not to mutate passed in objects
        options = Ext.apply({
            page: page,
            start: (page - 1) * me.pageSize,
            limit: me.pageSize,
            addRecords: !me.clearOnPageLoad
        }, options);


        if (me.buffered) {
            return me.loadToPrefetch(options);
        }
        me.read(options);
    },
    nextPage: function(options) {
        this.loadPage(this.currentPage + 1, options);
    },
    previousPage: function(options) {
        this.loadPage(this.currentPage - 1, options);
    },
    loadData: function(data, append) {
        var me = this,
            model = me.model,
            length = data.length,
            newData = [],
            i,
            record;


        for (i = 0; i < length; i++) {
            record = data[i];


            if (!(record.isModel)) {
                record = Ext.ModelManager.create(record, model);
            }
            newData.push(record);
        }


        me.loadRecords(newData, append ? me.addRecordsOptions : undefined);
    },
    loadRecords: function(records, options) {
        var me     = this,
            i      = 0,
            length = records.length,
            start,
            addRecords,
            snapshot = me.snapshot;


        if (options) {
            start = options.start;
            addRecords = options.addRecords;
        }


        if (!addRecords) {
            delete me.snapshot;
            me.clearData(true);
        } else if (snapshot) {
            snapshot.addAll(records);
        }


        me.data.addAll(records);


        if (start !== undefined) {
            for (; i < length; i++) {
                records[i].index = start + i;
                records[i].join(me);
            }
        } else {
            for (; i < length; i++) {
                records[i].join(me);
            }
        }


        /*
         * this rather inelegant suspension and resumption of events is required because both the filter and sort functions
         * fire an additional datachanged event, which is not wanted. Ideally we would do this a different way. The first
         * datachanged event is fired by the call to this.add, above.
         */
        me.suspendEvents();


        if (me.filterOnLoad && !me.remoteFilter) {
            me.filter();
        }


        if (me.sortOnLoad && !me.remoteSort) {
            me.sort(undefined, undefined, undefined, true);
        }


        me.resumeEvents();
        me.fireEvent('datachanged', me);
        me.fireEvent('refresh', me);
    },
    clearData: function(isLoad) {
        var me = this,
            records = me.data.items,
            i = records.length;


        while (i--) {
            records[i].unjoin(me);
        }
        me.data.clear();
        if (isLoad !== true || me.clearRemovedOnLoad) {
            me.removed.length = 0;
        }
    },
});

var itemsPerPage = 15;

var store = Ext.create('TREEGRIDS.store.TreeGridStore', {
    model: 'BatchTreeModel',
    remoteSort: true,
    pageSize: itemsPerPage,
    proxy: {
        type: 'ajax',
        enablePaging: true,
        url: 'getDetailsJson.do',
       	reader: {
           	type:'json',
            root: 'modelList',
            totalProperty: 'totalCount',
            model: 'BatchTreeModel',
            listeners: {
                exception: function(store, response, op) {
                	window.location = window.location.pathname;
                }
            }
        },
    },
    root: {
    	id:'node-root',
    	expanded: true
    },
});


Ext.onReady(function() {

    var tree = Ext.create('Ext.tree.Panel', {
        title: 'Job Status Viewer',
        width: $('#content').width(),
        height: $('#content').height(),
        renderTo: 'batchJobStatusViewer',
        collapsible: false,
        useArrows: true,
        rootVisible: false,
        store: store,
        multiSelect: false,
        columns: [{
        	xtype: 'treecolumn', //this is so we know which column will show the tree
            text: 'Name',
            width: 400,
            sortable: true,
            dataIndex: 'name'
        },{
            text: 'Id',
            width: 70,
            sortable: true,
            dataIndex: 'executionId',
            folderSort: true
        }, {
            text: 'Started',
            width: 150,
            sortable: true,
            dataIndex: 'startTime'
        }, {
        	text: 'Ended',
            width: 150,
            sortable: true,
            dataIndex: 'endTime'
        }, {
        	text: 'Status',
            width: 145,
            sortable: true,
            dataIndex: 'exitCode'
        }, {
        	text: 'Status Message',
            sortable: false,
            width: 400,
            dataIndex: 'exitMessage'
        }],
        bbar: { // bottom tool bar for paging
            xtype: 'pagingtoolbar',
            emptyMsg: "No Batch Job Executions to display",
            pageSize: itemsPerPage,
            store: store,
            displayInfo: true
        }
    });
    jQuery(window).bind('resize', function () {
   	 tree.setWidth($('#content').width());
   	 tree.setHeight($('#content').height());
	}).trigger('resize');
    
    store.loadPage(1);
    
    tree.on('sortchange', function() {
        tree.getStore().loadPage(1);
    });
});

</script>