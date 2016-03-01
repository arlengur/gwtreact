// Calculate number of channels per page based on channels viewport size
var ChannelsGridPaging = Object.freeze({
    // based on col-xxx-n in markup
    channelsPerRow: function (width, view) {
        switch(view) {
            case 1:
                if(width < 768) return 2;
                else if (width < 992) return 3;
                else if (width < 1200) return 4;
                else if (width < 2560) return 6;
                else return 12;
            case 2:
                if(width < 768) return 1;
                else if (width < 992) return 3;
                else if (width < 2560) return 4;
                else if (width < 3840) return 6;
                else return 12;
            case 3:
                if(width < 768) return 1;
                else if (width < 1336) return 2;
                else if (width < 2560) return 3;
                else if (width < 3840) return 6;
                else return 12;
            default:
                throw "Incorrect view number, should be one of [1, 2, 3]";
        }
    },
    timelineAtBottom: function(width) {
        return (width < 768 || width >= 2560);
    },
    channelHeight(width, view) {
        var playerHeight;
        switch(view) {
            case 1: return 38;
            case 2:
                playerHeight = (width / this.channelsPerRow(width, view) - 10) * 9 / 16 + 10;
                return 23 + playerHeight + 6;
            case 3:
                playerHeight = (width / this.channelsPerRow(width, view) / 2 - 10) * 9 / 16 + 10;
                if(this.timelineAtBottom(width)) {
                    return 23 + playerHeight + 155 + 6;
                } else {
                    return 23 + playerHeight + 6;
                }
            default:
                throw "Incorrect view number, should be one of [1, 2, 3]";
        }
    },
    channelsPerPage: function(width, height, view) {
        var rowsPerPage = Math.floor(height/this.channelHeight(width,view));
        if(rowsPerPage == 0) rowsPerPage = 1;
        return this.channelsPerRow(width, view) * rowsPerPage;
    }
});

export default ChannelsGridPaging;