package net.janczar.powertape.processor.model

import net.janczar.powertape.processor.TypeUtil
import net.janczar.powertape.processor.getName
import javax.lang.model.type.DeclaredType


class TypesMap {

    private val map = HashMap<String, Type>()

    fun add(type: Type): Type {
        map.put(type.type.getName(), type)
        return type
    }

    fun get(name: String): Type? = map[name]

    fun get(type: DeclaredType): Type? = map[type.getName()]

    fun getOrCreate(type: DeclaredType) = get(type) ?: add(Type(type))

    fun getAll() = map.values.toList()
}