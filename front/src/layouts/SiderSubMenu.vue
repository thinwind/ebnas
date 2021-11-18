<template functional>
<!-- <div>{{ menuInfo.children }}</div> -->
  <Submenu :name="menuInfo.path" >
    <span slot="title">
      <Icon v-if="menuInfo.meta.icon" :type="menuInfo.meta.icon" />
      <span>{{ menuInfo.meta.title }}</span>
    </span>
    <template v-for="item in menuInfo.children">
      <MenuItem v-if="!item.children" :key="item.path" :name="item.path" :to="item.path">
        <Icon v-if="item.meta.icon" :type="item.meta.icon" />
        <span>{{ item.meta.title }}</span>
      </MenuItem>
      <SiderSubMenu v-else :key="item.path" :menu-info="item" />
    </template>
  </Submenu>
</template>

<script>
import SiderSubMenu from "./SiderSubMenu";

export default {
  name: 'SiderSubMenu',
  components: {
    SiderSubMenu,
  },
  props: ['menuInfo'],
};
</script>
