rootProject.name = "airflux-commons"

include(":airflux-commons-text")
project(":airflux-commons-text").projectDir = file("./modules/text")

include(":airflux-commons-kotest-assertions")
project(":airflux-commons-kotest-assertions").projectDir = file("./modules/kotest/assertions")

include(":airflux-commons-collections")
project(":airflux-commons-collections").projectDir = file("./modules/collections")
