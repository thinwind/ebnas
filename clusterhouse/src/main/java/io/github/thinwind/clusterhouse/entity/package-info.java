/*
 * Copyright ShangYh
 */
@org.hibernate.annotations.GenericGenerator(
  name = Consts.ID_GENERATOR,
  strategy = "enhanced-sequence",
  parameters = {
     @org.hibernate.annotations.Parameter(
        name = "sequence_name",
        value = Consts.ID_GENERATOR
     ),
     @org.hibernate.annotations.Parameter(
        name = "initial_value",
        value = "100000"
     )
})
package io.github.thinwind.clusterhouse.entity;

import io.github.thinwind.clusterhouse.misc.Consts;
