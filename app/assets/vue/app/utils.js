export default class Utils {

  static toPortfolioItem (type) {
    return (item) => ({
      description: item.description,
      href: `/${type}/${item.id}`,
      id: item.id,
      image: item.image ?('/api/v1/images/' + item.image) : '/assets/images/img3.jpg',
      title: item.name
    })
  }

}
