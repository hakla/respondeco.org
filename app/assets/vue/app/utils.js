export default class Utils {

  static toPortfolioItem (item) {
    return {
      description: item.description,
      id: item.id,
      image: item.image ?('/api/v1/images/' + item.image) : '/assets/images/img3.jpg',
      title: item.name
    }
  }

}
