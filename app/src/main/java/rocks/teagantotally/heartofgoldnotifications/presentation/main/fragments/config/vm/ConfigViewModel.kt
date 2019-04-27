package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.vm

import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.PreferenceFragmentCompat
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.Configuration
import timber.log.Timber
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

open class ConfigViewModel<ConfigurationType : Configuration>(
    private val fragment: PreferenceFragmentCompat
) {
    open val preferenceTypeMap: Map<Int, KClass<*>> = mapOf()
    private val preferenceViewMap: MutableMap<Int, Any> = mutableMapOf()

    open fun populate() {}
    open fun retrieve(): ConfigurationType? = null
    open suspend fun save() {}
    open fun isValid(): Boolean = false

    protected fun <ValueType> setValue(preferenceKey: Int, value: ValueType) {
        preferenceViewMap[preferenceKey]
            ?.let { preference ->
                when (preferenceTypeMap[preferenceKey]) {
                    EditTextPreference::class ->
                        (preference as? EditTextPreference)
                            ?.text = value.toString()
                    SwitchPreference::class ->
                        (preference as? SwitchPreference)
                            ?.isChecked = (value as? Boolean ?: false)
                }
            }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    protected fun <ValueType : Any> getValue(preferenceKey: Int, valueClass: KClass<ValueType>): ValueType? =
        preferenceViewMap[preferenceKey]
            ?.let { preference ->
                when (preferenceTypeMap[preferenceKey]) {
                    EditTextPreference::class ->
                        (preference as? EditTextPreference)?.text
                    SwitchPreference::class ->
                        (preference as? SwitchPreference)?.isChecked
                    else -> null
                }
            }
            ?.let { valueClass.cast(it) }

    fun initialize() {
        preferenceTypeMap
            .forEach { key, preferenceType ->
                preferenceViewMap[key] =
                    fragment
                        .findPreference(fragment.getString(key))
                        .let {
                            Timber.d(it.javaClass.name)
                            preferenceType.cast(it)
                        }
            }
    }
}