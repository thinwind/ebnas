<template>
  <div class="layout">
    <Layout>
      <Sider
        ref="side1"
        hide-trigger
        collapsible
        width="230px"
        :collapsed-width="78"
        v-model="isCollapsed"
      >
        <div class="layout-logo-left">后台管理</div>
        <SiderMenu />
      </Sider>
      <Layout>
        <Header
          :style="{
            background: '#fff',
            boxShadow: '0 1px 1px 1px rgba(0,0,0,.1)',
            padding: '0px',
          }"
        >
          <!-- <Icon @click.native="collapsedSider" :class="rotateIcon" :style="{margin: '0 20px'}" type="md-menu" size="24"></Icon> -->
          <HeaderContent />
        </Header>

        <Content :style="{ padding: '0 16px 16px' }">
          <Breadcrumb :style="{ margin: '16px 0' }">
            <BreadcrumbItem>集群管理</BreadcrumbItem>
            <BreadcrumbItem>集群节点</BreadcrumbItem>
          </Breadcrumb>
          <Card>
            <div style="height: 600px">
              <router-view></router-view>
            </div>
          </Card>
        </Content>
        <Footer class="layout-footer-center"><FooterContent /></Footer>
      </Layout>
    </Layout>
    <!-- <Authorized :authority="['admin']">
    <SettingDrawer /> -->
    <!-- </Authorized> -->
  </div>
</template>

<script>
import HeaderContent from "./Header";
import FooterContent from "./Footer";
// import SettingDrawer from "../components/SettingDrawer";
import SiderMenu from "./SiderMenu";

export default {
  name: "BasicLayout",
  components: {
    HeaderContent,
    FooterContent,
    // SettingDrawer,
    SiderMenu,
  },
  data() {
    return {
      isCollapsed: false,
    };
  },
  computed: {
    rotateIcon() {
      return ["menu-icon", this.isCollapsed ? "rotate-icon" : ""];
    },
    menuitemClasses() {
      return ["menu-item", this.isCollapsed ? "collapsed-menu" : ""];
    },
  },
  methods: {
    collapsedSider() {
      this.$refs.side1.toggleCollapse();
    },
  },
};
</script>

<style scoped>
.layout {
  border: 1px solid #d7dde4;
  background: #f5f7f9;
  position: relative;
  border-radius: 4px;
  overflow: hidden;
}
.layout-header-bar {
  background: #fff;
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
}

.layout-logo-left {
  width: 90%;
  height: 64px;
  line-height: 64px;
  color: #fff;
  overflow: hidden;
  text-align: center;
}

.menu-item span {
  display: inline-block;
  overflow: hidden;
  width: 69px;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: bottom;
  transition: width 0.2s ease 0.2s;
}
.menu-item i {
  transform: translateX(0px);
  transition: font-size 0.2s ease, transform 0.2s ease;
  vertical-align: middle;
  font-size: 16px;
}
.collapsed-menu span {
  width: 0px;
  transition: width 0.2s ease;
}
.collapsed-menu i {
  transform: translateX(5px);
  transition: font-size 0.2s ease 0.2s, transform 0.2s ease 0.2s;
  vertical-align: middle;
  font-size: 22px;
}
.menu-icon {
  transition: all 0.3s;
}

.rotate-icon {
  transform: rotate(-90deg);
}
.menu-icon {
  transition: all 0.3s;
}
.rotate-icon {
  transform: rotate(-90deg);
}
.layout-footer-center {
  text-align: center;
}
</style>