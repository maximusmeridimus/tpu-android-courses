package ru.tpu.courses.lab3;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Кэш списка студентов в оперативной памяти. Самый быстрый тип кэша, но ограничен размерами
 * оперативной памяти и имеет время жизни равное жизни приложения. Т.е. если приложение будет
 * выгружено из оперативной памяти - то все данные из этого кэша пропадут.
 * Такой тип кэшей используется для временных данных, потеря которых не важна, в большинстве случаев
 * чтобы не делать дополнительные запросы на сервер.
 */
public class GroupsCache {

    private static GroupsCache instance;

    public static GroupsCache getInstance() {
        if (instance == null) {
            synchronized (GroupsCache.class) {
                if (instance == null) {
                    instance = new GroupsCache();
                }
            }
        }
        return instance;
    }

    private Set<Group> groups = new LinkedHashSet<>();

    private GroupsCache() {
    }

    @NonNull
    public List<Group> getGroups() {
        return new ArrayList<>(groups);
    }

    public void addGroup(@NonNull Group group) {
        groups.add(group);
    }

    public boolean contains(@NonNull Group group) {
        return groups.contains(group);
    }
}
