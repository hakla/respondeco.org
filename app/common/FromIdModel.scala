package common

/**
  * Created by Klaus on 12.03.2017.
  */
trait FromIdModel[ReadModel, PublicModel] {

    def from (obj: ReadModel): PublicModel

}
