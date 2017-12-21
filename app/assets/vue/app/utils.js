export default class Utils {

  static toPortfolioItem (type) {
    return (item) => ({
      description: item.description,
      href: `/${type}/${item.id}`,
      id: item.id,
      image: item.logo ?('/api/v1/images/' + item.logo) : '/assets/images/img3.jpg',
      title: item.name
    })
  }

}
