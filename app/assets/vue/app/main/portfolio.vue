<template>
  <div class="cube-portfolio container margin-bottom-60">
    <div class="content-xs">
      <div :id="'filters_' + _uid" class="cbp-l-filters-text content-xs">
      </div><!--/end Filters Container-->
    </div>

    <div :id="'grid_' + _uid" class="cbp-l-grid-agency">
      <div class="cbp-item" v-for="item in items">
        <div class="cbp-caption margin-bottom-20">
          <div class="cbp-caption-defaultWrap">
            <img :src="item.image" alt="">
          </div>
          <div class="cbp-caption-activeWrap">
            <div class="cbp-l-caption-alignCenter">
              <div class="cbp-l-caption-body">
                <ul class="link-captions no-bottom-space">
                  <li><a href="portfolio_single_item.html"><i class="rounded-x fa fa-link"></i></a></li>
                  <li><a href="assets/img/main/img3.jpg" class="cbp-lightbox" data-title="Design Object"><i class="rounded-x fa fa-search"></i></a></li>
                </ul>
              </div>
            </div>
          </div>
        </div>
        <div class="cbp-title-dark">
          <div class="cbp-l-grid-agency-title">{{ item.title }}</div>
          <div class="cbp-l-grid-agency-desc">{{ item.description }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'portfolio',

    props: ['items'],

    updated () {
      setTimeout(() => {
        let vm = this

        var gridContainer = $(`#grid_${this._uid}`),
          filtersContainer = $(`#filter_${this._uid}`),
          wrap, filtersCallback;


        /*********************************
         init cubeportfolio
         *********************************/
        gridContainer.cubeportfolio({
          layoutMode: 'grid',
          rewindNav: true,
          scrollByPage: false,
          defaultFilter: '*',
          animationType: 'slideLeft',
          gapHorizontal: 20,
          gapVertical: 20,
          gridAdjustment: 'responsive',
          mediaQueries: [{
            width: 800,
            cols: 3
          }, {
            width: 500,
            cols: 2
          }, {
            width: 320,
            cols: 1
          }],
          caption: 'zoom',
          displayType: 'lazyLoading',
          displayTypeSpeed: 100
        });


        /*********************************
         add listener for filters
         *********************************/
        if (filtersContainer.hasClass('cbp-l-filters-dropdown')) {
          wrap = filtersContainer.find('.cbp-l-filters-dropdownWrap');

          wrap.on({
            'mouseover.cbp': function() {
              wrap.addClass('cbp-l-filters-dropdownWrap-open');
            },
            'mouseleave.cbp': function() {
              wrap.removeClass('cbp-l-filters-dropdownWrap-open');
            }
          });

          filtersCallback = function(me) {
            wrap.find('.cbp-filter-item').removeClass('cbp-filter-item-active');
            wrap.find('.cbp-l-filters-dropdownHeader').text(me.text());
            me.addClass('cbp-filter-item-active');
            wrap.trigger('mouseleave.cbp');
          };
        } else {
          filtersCallback = function(me) {
            me.addClass('cbp-filter-item-active').siblings().removeClass('cbp-filter-item-active');
          };
        }

        filtersContainer.on('click.cbp', '.cbp-filter-item', function() {
          var me = $(this);

          if (me.hasClass('cbp-filter-item-active')) {
            return;
          }

          // get cubeportfolio data and check if is still animating (reposition) the items.
          if (!$.data(gridContainer[0], 'cubeportfolio').isAnimating) {
            filtersCallback.call(null, me);
          }

          // filter the items
          gridContainer.cubeportfolio('filter', me.data('filter'), function() {});
        });


        /*********************************
         activate counter for filters
         *********************************/
        gridContainer.cubeportfolio('showCounter', filtersContainer.find('.cbp-filter-item'), function() {
          // read from url and change filter active
          var match = /#cbpf=(.*?)([#|?&]|$)/gi.exec(location.href),
            item;
          if (match !== null) {
            item = filtersContainer.find('.cbp-filter-item').filter('[data-filter="' + match[1] + '"]');
            if (item.length) {
              filtersCallback.call(null, item);
            }
          }
        });
      }, 0)
    }
  }
</script>
