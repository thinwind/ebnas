<template>
  <div style="width: 230px">
    <Menu
      theme="dark"
      :active-name="selectedKeys"
      :open-names="openKeys"
      width="230px"
    >
      <template v-for="item in menuData">
        <MenuItem
          v-if="!item.item.children"
          :key="item.item.path"
          :name="item.item.path"
          :to="item.item.path"
        >
          <Icon v-if="item.item.meta.icon" :type="item.item.meta.icon" />
          <span>{{ item.item.meta.title }}</span>
        </MenuItem>
        <SiderSubMenu v-else :key="item.item.path" :menu-info="item.item" />
      </template>
    </Menu>
  </div>
</template>

<script>
import SiderSubMenu from "./SiderSubMenu";
import { check } from "../utils/auth";

export default {
  components: {
    SiderSubMenu,
  },
  watch: {
    "$route.path": function (val) {
      this.selectedKeys = this.selectedKeysMap[val];
      this.openKeys = this.openKeysMap[val];
    },
  },
  data() {
    this.selectedKeysMap = {};
    this.openKeysMap = {};
    const menuData = this.getMenuData(this.$router.options.routes);
    return {
      collapsed: false,
      menuData,
      selectedKeys: this.selectedKeysMap[this.$route.path],
      openKeys: this.openKeysMap[this.$route.path],
    };
  },
  methods: {
    getMenuData(routes = [], parentKeys = [], selectedKey) {
      const menuData = [];
      for (let item of routes) {
        if (item.meta && item.meta.authority && !check(item.meta.authority)) {
          break;
        }
        if (item.name && !item.hideInMenu) {
          this.openKeysMap[item.path] = parentKeys;
          this.selectedKeysMap[item.path] = item.path || selectedKey;
          const newItem = { item };
          delete newItem.children;
          if (item.children && !item.hideChildrenInMenu) {
            newItem.children = this.getMenuData(item.children, [
              ...parentKeys,
              item.path,
            ]);
          } else {
            this.getMenuData(
              item.children,
              selectedKey ? parentKeys : [...parentKeys, item.path],
              selectedKey || item.path
            );
          }

          menuData.push(newItem);
        } else if (
          !item.hideInMenu &&
          !item.hideChildrenInMenu &&
          item.children
        ) {
          menuData.push(
            ...this.getMenuData(item.children, [...parentKeys, item.path])
          );
        }
      }

      return menuData;
    },
  },
};
</script>

<style>
</style>