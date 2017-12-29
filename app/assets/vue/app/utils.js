export default class Utils {

  static toPortfolioItem (type) {
    return (item) => ({
      description: item.description,
      href: `/${type}/${item.id}`,
      id: item.id,
      image: item.logo ?('/api/v1/images/' + item.logo) : '/assets/images/demo-square.jpg',
      title: item.name
    })
  }

  static projectToPortfolioItem (item) {
    return {
      description: item.description,
      href: `/projects/${item.id}`,
      id: item.id,
      image: item.image ?('/api/v1/images/' + item.image) : '/assets/images/demo-square.jpg',
      title: item.name
    }
  }

}

export const RouteHelper = {
  meta: {
    noFooter: {
      footer: false,
    },

    noHeader: {
      header: false
    },

    scrollToHero: {
      scroll: '.unify-hero'
    },

    scrollToListGroup: {
      scroll: false // for now
    },

    scrollToTop: {
      scroll: (to) => {
        if (!to.query.page) {
          return 0
        } else {
          return false
        }
      }
    }
  },

  scrollBehavior (to, from, savedPosition) {
    setTimeout(() => {
      if (to.meta.scroll != null && to.meta.scroll !== false) {
        let position = 0

        if (savedPosition) {
          position = savedPosition.y
        } else if (typeof to.meta.scroll === 'string') {
          let $el = $(to.meta.scroll)

          if ($el.length > 0) {
            try {
              position = $el.offset().top
            } catch (e) {
              return
            }
          }
        } else if (typeof to.meta.scroll === 'number') {
          position = to.meta.scroll
        } else if (typeof to.meta.scroll === 'function') {
          position = to.meta.scroll(to, from, savedPosition)
        }

        if (position !== false) {
          $('html, body').animate({
            scrollTop: position
          })
        }
      }
    }, 10)
  }
}
