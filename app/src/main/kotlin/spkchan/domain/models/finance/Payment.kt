package spkchan.domain.models.finance

import java.time.LocalDateTime

inline fun <reified T : PaymentCategory> T.subGenres() = T::class.nestedClasses.map { it.objectInstance!! as PaymentSubGenre }
fun List<PaymentSubGenre>.valueOf(id: Long) = first { it.id == id }

class Payment(
    recordId: Long,
    transactionDateTime: LocalDateTime,
    amount: Int,
    place: String,
    comment: String,
    val category: PaymentCategory,
    val subGenre: PaymentSubGenre,
) : FinancialRecord(
    recordId,
    transactionDateTime,
    amount,
    place,
    comment,
)

sealed class PaymentSubGenre(val id: Long)
sealed class PaymentCategory(val id: Long) {
    companion object {
        fun values() = PaymentCategory::class.sealedSubclasses.map { it.objectInstance!! }
        fun valueOf(id: Long) = values().first { it.id == id }
    }
    object Food : PaymentCategory(101) {
        object Groceries : PaymentSubGenre(10101)
        object Cafe : PaymentSubGenre(10102)
        object Breakfast : PaymentSubGenre(10103)
        object Lunch : PaymentSubGenre(10104)
        object Dinner : PaymentSubGenre(10105)
        object Other : PaymentSubGenre(10199)
    }
    object DailyGoods : PaymentCategory(102) {
        object Consumable : PaymentSubGenre(10201)
        object ChildRelated : PaymentSubGenre(10202)
        object PetRelated : PaymentSubGenre(10203)
        object Tobacco : PaymentSubGenre(10204)
        object Other : PaymentSubGenre(10299)
    }
    object Transport : PaymentCategory(103) {
        object Train : PaymentSubGenre(10301)
        object Taxi : PaymentSubGenre(10302)
        object Bus : PaymentSubGenre(10303)
        object Airfares : PaymentSubGenre(10304)
        object Other : PaymentSubGenre(10399)
    }
    object PhoneOrNet : PaymentCategory(104) {
        object CellPhone : PaymentSubGenre(10401)
        object FixedLinePhones : PaymentSubGenre(10402)
        object InternetRelated : PaymentSubGenre(10403)
        object TVLicense : PaymentSubGenre(10404)
        object Delivery : PaymentSubGenre(10405)
        object PostcardOrStamps : PaymentSubGenre(10406)
        object Other : PaymentSubGenre(10499)
    }
    object Utilities : PaymentCategory(105) {
        object Water : PaymentSubGenre(10501)
        object Electricity : PaymentSubGenre(10502)
        object Gas : PaymentSubGenre(10503)
        object Other : PaymentSubGenre(10599)
    }
    object Home : PaymentCategory(106) {
        object Rent : PaymentSubGenre(10601)
        object Mortgage : PaymentSubGenre(10602)
        object Furniture : PaymentSubGenre(10603)
        object HomeElectronics : PaymentSubGenre(10604)
        object Reform : PaymentSubGenre(10605)
        object HomeInsurance : PaymentSubGenre(10606)
        object Other : PaymentSubGenre(10699)
    }
    object Socializing : PaymentCategory(107) {
        object Party : PaymentSubGenre(10701)
        object Gifts : PaymentSubGenre(10702)
        object CeremonialEvents : PaymentSubGenre(10703)
        object Other : PaymentSubGenre(10799)
    }
    object Hobbies : PaymentCategory(108) {
        object Leisure : PaymentSubGenre(10801)
        object Events : PaymentSubGenre(10802)
        object Cinema : PaymentSubGenre(10803)
        object Music : PaymentSubGenre(10804)
        object Cartoon : PaymentSubGenre(10805)
        object Books : PaymentSubGenre(10806)
        object Games : PaymentSubGenre(10807)
        object Other : PaymentSubGenre(10899)
    }
    object Education : PaymentCategory(109) {
        object AdultTuitionFees : PaymentSubGenre(10901)
        object Newspapers : PaymentSubGenre(10902)
        object ReferenceBook : PaymentSubGenre(10903)
        object ExaminationFee : PaymentSubGenre(10904)
        object Tuition : PaymentSubGenre(10905)
        object StudentInsurance : PaymentSubGenre(10906)
        object CramSchool : PaymentSubGenre(10907)
        object Other : PaymentSubGenre(10999)
    }
    object Medical : PaymentCategory(110) {
        object Hospital : PaymentSubGenre(11001)
        object Prescription : PaymentSubGenre(11002)
        object LifeInsurance : PaymentSubGenre(11003)
        object MedicalInsurance : PaymentSubGenre(11004)
        object Other : PaymentSubGenre(11099)
    }
    object Fashion : PaymentCategory(111) {
        object Clothes : PaymentSubGenre(11101)
        object Accessories : PaymentSubGenre(11102)
        object Underwear : PaymentSubGenre(11103)
        object GymOrHealth : PaymentSubGenre(11104)
        object BeautyOrSalon : PaymentSubGenre(11105)
        object Cosmetics : PaymentSubGenre(11106)
        object EstheticClinic : PaymentSubGenre(11107)
        object Laundry : PaymentSubGenre(11108)
        object Other : PaymentSubGenre(11199)
    }
    object Automobile : PaymentCategory(112) {
        object Gasoline : PaymentSubGenre(11201)
        object Parking : PaymentSubGenre(11202)
        object AutoInsurance : PaymentSubGenre(11203)
        object AutoTax : PaymentSubGenre(11204)
        object AutoLoan : PaymentSubGenre(11205)
        object AccreditationFees : PaymentSubGenre(11206)
        object Tolls : PaymentSubGenre(11207)
        object Other : PaymentSubGenre(11299)
    }
    object Taxes : PaymentCategory(113) {
        object Pension : PaymentSubGenre(11301)
        object IncomeTax : PaymentSubGenre(11302)
        object SalesTax : PaymentSubGenre(11303)
        object ResidenceTax : PaymentSubGenre(11304)
        object CorporateTax : PaymentSubGenre(11305)
        object Other : PaymentSubGenre(11399)
    }
    object BigOutlay : PaymentCategory(114) {
        object Travel : PaymentSubGenre(11401)
        object Home : PaymentSubGenre(11402)
        object Automotive : PaymentSubGenre(11403)
        object Motorbike : PaymentSubGenre(11404)
        object Wedding : PaymentSubGenre(11405)
        object Childbirth : PaymentSubGenre(11406)
        object Nursing : PaymentSubGenre(11407)
        object Furniture : PaymentSubGenre(11408)
        object HomeElectronics : PaymentSubGenre(11409)
        object Other : PaymentSubGenre(11499)
    }
    object Other : PaymentCategory(199) {
        object Allowance : PaymentSubGenre(19901)
        object PocketMoney : PaymentSubGenre(19902)
        object Unaccounted : PaymentSubGenre(19903)
        object AdvancesPaid : PaymentSubGenre(19904)
        object Uncategorized : PaymentSubGenre(19905)
        object DebitCach : PaymentSubGenre(19906)
        object DebitCard : PaymentSubGenre(19907)
        object ChargeDebit : PaymentSubGenre(19908)
        object Other : PaymentSubGenre(19999)
    }
}
