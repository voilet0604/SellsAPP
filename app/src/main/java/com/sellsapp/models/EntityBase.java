/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sellsapp.models;

import com.lidroid.xutils.db.annotation.Id;


/**
 * 用于DBUtils 创建数据库表
 * @author violet
 *
 */
public abstract class EntityBase {


    @Id // 如果主键没有命名名为id或_id的时，需要为主键添加此注解
    //@NoAutoIncrement // int,long类型的id默认自增，不想使用自增时添加此注解
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
