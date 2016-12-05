// Copyright (c) 2013 Adobe Systems Incorporated. All rights reserved.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

var gcviewCustomization = function() {
    var StandardPageSize = 1024 * 1024;

    var msFormatter = customizationShared.msFromSecFormatter;
    var secFormatter = customizationShared.secFromSecFormatter;
    var byteFormatter = customizationShared.bytesFromBytesFormatter;
    var kbFormatter = customizationShared.kbsFromBytesFormatter;
    var mbFormatter = customizationShared.mbsFromBytesFormatter;
    var boolFormatter = customizationShared.boolFormatter;

    var countFormatter = {
        transform : function(value) { return value; },
        format    : function(value) { return value; },
        unitStr   : 'count'
    };

    var isCommittedFormatter = {
        transform : function(value) { return value; },
        format    : function(value) {
            return (value) ? 'Committed' : 'Not Commmited'
        },
        unitStr   : ''
    };

    var pageSetTypeFormatter = {
        transform : function(value) { return value; },
        format    : function(value) {
            if (value == 0) 
              return 'Empty Page';
            else if (value == 1)
              return 'LargeObject';
            else
              return 'Run';
        },
        unitStr   : ''
    };

    var events = {
    };

    var spaces = {
        'Summary' : {
            Groups : { 'GC Summary' : {  SlotName : 'GC ID',
                                         Labels   : 'GC Type' } },

            Data : {
                'GC Type'             : { ExcludeFromMenu : true },
                'GC Count'            : { Formatter : countFormatter },
                'Heap Capacity'       : { Formatter : mbFormatter },
                'Heap Footprint'      : { Formatter : mbFormatter },
                'Heap Allocated Size' : { Formatter : mbFormatter },
                'Heap Allocated Object Count' : { Formatter : countFormatter },
            },

            ExpandAtStart : true,

            Histograms : [
                { Name    : 'GC Info',
                  Type    : 'bars',
                  Stacked : false,
                  Labels : 'GC Type',
                  Data : {
                      Names     : [ 'GC Count' ],
                      Formatter : countFormatter
                  }
                },

                { Name    : 'Heap Info',
                  Type    : 'bars',
                  Stacked : false,
                  Data : {
                      Names     : [ 'Heap Capacity',
                                    'Heap Footprint',
                                    'Heap Allocated Size'],
                      Formatter : mbFormatter
                  }
                }
            ]
        },

        'main rosalloc space' : {
            Groups : {
                null                      : { SlotName : 'Pages Kind'             },
                'Empty/Release Kind'      : { SlotName : 'Empty/Release Pages ID' },
                'Large Object Kind'       : { SlotName : 'Large Object ID'        },
                'Run Kind'                : { SlotName : 'Run ID'             },
                'Run Distribution'        : { SlotName : 'Bracket ID'     },
            },

            Data : {
                'Space Capacity'                 : { Formatter : mbFormatter },
                'Space Footprint'                : { Formatter : mbFormatter },
                'Space Allocated Size'           : { Formatter : mbFormatter },
                'Space Allocated Object Count'   : { Formatter : countFormatter },
                'Count of Empty Kind'            : { Formatter : countFormatter },
                'Count of Release Kind'          : { Formatter : countFormatter },
                'Count of Empty/Release Kind'    : { Formatter : countFormatter },
                'Count of Large Object kind'     : { Formatter : countFormatter },
                'Count of Run Kind'              : { Formatter : countFormatter },
                'Continous Pages Kind'           : { ExcludeFromMenu : true },
                'Empty/Release Pages Size'       : { Formatter : kbFormatter},
                'Empty/Release Pages Address Range'  : { ExcludeFromMenu : true },
                'Large Object Size'              : { Formatter : kbFormatter },
                'Large Object Address Range'     : { ExcludeFromMenu : true },
                'Run Size'                       : { ExcludeFromMenu : true ,
                                                     Formatter : kbFormatter },
                'Run Address Range'              : { ExcludeFromMenu : true },
                'Run Allocated Size'             : { Formatter : byteFormatter },
                'Run Allocated Object Count'     : { Formatter : countFormatter },
                'Run Bracket Size'               : { ExcludeFromMenu : true ,
                                                     Formatter : byteFormatter  },
                'Run total slots Num'            : { ExcludeFromMenu : true,
                                                     Formatter : countFormatter },
                'Run is Local'                   : { Formatter : boolFormatter },
                'Run is to be Bulk Freed'        : { Formatter : boolFormatter },
                'Run is Full'                    : { Formatter : boolFormatter },
                'Bracket Size'                   : { ExcludeFromMenu : true ,
                                                     Formatter : byteFormatter },
                'Population Of The Same Bracket Size'
                                                 : { ExcludeFromMenu : true ,
                                                     Formatter : countFormatter },
                'Total Allocated Bytes Of The Same Bracket Size'
                                                 : { ExcludeFromMenu : true ,
                                                     Formatter : kbFormatter   },
            },

            Histograms : [
                { Name    : 'Space Info',
                  Type    : 'bars',
                  Stacked : false,
                  Data : {
                      Names     : [ 'Space Capacity',
                                    'Space Footprint' ,
                                    'Space Allocated Size'],
                      Formatter : mbFormatter
                  }
                },

                { Name    : 'Page Set Distribution',
                  Type    : 'bars',
                  Stacked : false,
                  Data : {
                      Names     : [ 'Empty Pages Set Count',
                                    'Large Object Set Count' ,
                                    'Run Set Count'],
                      Formatter : countFormatter
                  }
                },

                { Name    : 'Run Distribution',
                  Type    : 'bars',
                  Stacked : false,
                  Data : {
                      Names     : [ 'Population Of The Same Bracket Size' ],
                      Formatter : countFormatter
                  },
                  Data1 : {
                      Names     : [ 'Total Allocated Bytes Of The Same Bracket Size' ],
                      Formatter : kbFormatter
                  }
                }
            ]
        },

        'mem map large object space' : {
            Groups : { 
                null               : { SlotName : 'Large Object Info' },
               'Large Object Info' : { SlotName : 'Large Object ID' }, 
               'MemMap Info'       : { SlotName : 'MemMap ID'       }
            }, 

            Data : {
                'Bytes Allocated'         : { Formatter : kbFormatter },
                'Objects Allocated'       : { Formatter : countFormatter },
                'Total Bytes Allocated'   : { Formatter : mbFormatter },
                'Total Object Allocated'  : { Formatter : countFormatter },
                'Object Address Range'    : { ExcludeFromMenu : true },
                'Object Length'           : { Formatter : kbFormatter },
                'MemMap Range'            : { ExcludeFromMenu : true },
                'MemMap Size'             : { Formatter : kbFormatter },
                'Is MemMap'               : { Formatter : boolFormatter }
            }
        },
    };

    return { Events : events,
             Spaces : spaces };
}();
