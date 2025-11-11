<template>
  <div class="map-page">
    <nav class="main-nav">
      <router-link to="/">行程规划</router-link>
      <router-link to="/expenses">旅游开销</router-link>
    </nav>
    <h1>地图定位导航</h1>
    <div id="mapContainer" style="width: 100%; height: 500px;"></div>
    <div class="search-box">
      <input type="text" v-model="searchKeyword" placeholder="请输入地点" />
      <button @click="searchLocation">搜索</button>
    </div>
    <div class="point-selection-controls">
      <button @click="activatePointSetting('start')" :class="{ active: isSettingStart }">设置起点</button>
      <button @click="activatePointSetting('end')" :class="{ active: isSettingEnd }">设置终点</button>
      <button @click="clearPoints">清除标记</button>
      <button @click="getRoute" :disabled="!startPoint || !endPoint">获取路线</button>
    </div>
    <div v-if="startPoint" class="selected-point-info">起点: {{ startPoint.lng }}, {{ startPoint.lat }}</div>
    <div v-if="endPoint" class="selected-point-info">终点: {{ endPoint.lng }}, {{ endPoint.lat }}</div>
    <div id="r-result" class="route-panel"></div>
  </div>
</template>

<script>
export default {
  name: 'MapPage',
  data() {
    return {
      map: null,
      searchKeyword: '',
      startPoint: null,
      endPoint: null,
      isSettingStart: false,
      isSettingEnd: false,
      startMarker: null,
      endMarker: null,
    };
  },
  mounted() {
    this.loadBaiduMapScript();
  },
  methods: {
    loadBaiduMapScript() {
      const AK = 'CW452WC0MtvrVEn7NfrFcHP9VdclwQOZ';
      //  // 请替换为您的百度地图API密钥
      // const AK = process.env.VUE_APP_BAIDU_MAP_AK; // 从环境变量获取百度地图API密钥
      if (!AK) {
        console.error('Baidu Map API Key (VUE_APP_BAIDU_MAP_AK) is not set in environment variables.');
        return;
      }
      const script = document.createElement('script');
      script.src = `//api.map.baidu.com/api?v=3.0&ak=${AK}&callback=initBaiduMap`; // 移除 &s=1 参数
      script.onload = () => {
        window.initBaiduMap = this.initMap;
      };
      document.head.appendChild(script);
    },
    initMap() {
      this.map = new BMap.Map('mapContainer'); // 创建地图实例
      const point = new BMap.Point(116.404, 39.915); // 默认中心点：北京
      this.map.centerAndZoom(point, 15); // 设置中心点和缩放级别
      this.map.enableScrollWheelZoom(true); // 启用滚轮缩放

      // 添加地图点击事件监听
      this.map.addEventListener('click', this.handleMapClick);
    },
    handleMapClick(e) {
      if (this.isSettingStart) {
        this.setPoint('start', e.point);
      } else if (this.isSettingEnd) {
        this.setPoint('end', e.point);
      }
    },
    activatePointSetting(type) {
      this.isSettingStart = false;
      this.isSettingEnd = false;
      if (type === 'start') {
        this.isSettingStart = true;
        alert('请在地图上点击选择起点。');
      } else if (type === 'end') {
        this.isSettingEnd = true;
        alert('请在地图上点击选择终点。');
      }
    },
    setPoint(type, point) {
      if (type === 'start') {
        this.startPoint = point;
        if (this.startMarker) {
          this.map.removeOverlay(this.startMarker);
        }
        this.startMarker = new BMap.Marker(point);
        this.map.addOverlay(this.startMarker);
        this.isSettingStart = false;
      } else if (type === 'end') {
        this.endPoint = point;
        if (this.endMarker) {
          this.map.removeOverlay(this.endMarker);
        }
        this.endMarker = new BMap.Marker(point);
        this.map.addOverlay(this.endMarker);
        this.isSettingEnd = false;
      }
    },
    clearPoints() {
      if (this.startMarker) {
        this.map.removeOverlay(this.startMarker);
        this.startMarker = null;
      }
      if (this.endMarker) {
        this.map.removeOverlay(this.endMarker);
        this.endMarker = null;
      }
      this.startPoint = null;
      this.endPoint = null;
      this.isSettingStart = false;
      this.isSettingEnd = false;
      // 清除所有路线覆盖物
      this.map.clearOverlays();
    },
    getRoute() {
      if (!this.startPoint || !this.endPoint) {
        alert('请先设置起点和终点。');
        return;
      }

      console.log('起点:', this.startPoint);
      console.log('终点:', this.endPoint);

      // 每次规划路线前，只清除之前的路线覆盖物，保留起点和终点标记
      // 百度地图的Driving服务会自动添加路线覆盖物
      // 如果需要清除所有覆盖物（包括标记），应该调用clearPoints方法
      // this.map.clearOverlays(); // 移除此行，避免清除Driving服务即将绘制的路线

       const driving = new BMap.DrivingRoute(this.map, {
         renderOptions: {
           map: this.map,
           autoViewport: true,
           panel: "r-result", // 结果面板，可以显示详细路线信息
         },
         oncomplete: (results) => {
           console.log('oncomplete callback triggered.');
           if (driving.getStatus() === BMAP_STATUS_SUCCESS) {
             console.log('路线规划成功:', results);
             // 路线规划成功，地图上会自动显示路线
           } else {
             console.error('路线规划失败，状态码:', driving.getStatus());
             console.error('路线规划结果:', driving.getResults());
             alert('路线规划失败，请检查起点和终点是否有效。');
           }
         },
       });

       driving.search(this.startPoint, this.endPoint);
    },
    searchLocation() {
      if (!this.map) {
        alert('地图未加载完成，请稍后再试。');
        return;
      }
      const local = new BMap.LocalSearch(this.map, {
        renderOptions: { map: this.map, autoViewport: true },
        onSearchComplete: (results) => {
          if (local.getStatus() === BMAP_STATUS_SUCCESS) {
            console.log('搜索结果:', results);
            // 可以在这里添加逻辑，例如将搜索结果的第一个点设置为起点或终点
          } else {
            alert('未找到相关地点。');
          }
        },
      });
      local.search(this.searchKeyword);
    },
  },
};
</script>

<style scoped>
.map-page {
  padding: 20px;
  text-align: center;
}

.main-nav {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
  gap: 10px; /* Add space between buttons */
}

.main-nav a {
  display: inline-block;
  padding: 10px 20px;
  background-color: #42b983; /* Green background */
  color: white; /* White text */
  text-decoration: none;
  border-radius: 5px;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

.main-nav a:hover {
  background-color: #369f75; /* Darker green on hover */
}

#allmap {
  width: 100%;
  height: 500px;
  margin-top: 20px;
}
#mapContainer {
  margin: 20px auto;
  border: 1px solid #ddd;
  border-radius: 8px;
}
.search-box,
.point-selection-controls {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 10px;
}
.search-box input,
.point-selection-controls button {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-right: 10px;
}
.search-box button,
.point-selection-controls button {
  padding: 8px 15px;
  background-color: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.search-box button:hover,
.point-selection-controls button:hover {
  background-color: #369f75;
}
.point-selection-controls button.active {
  background-color: #2c3e50;
}
.selected-point-info {
  margin-top: 10px;
  font-weight: bold;
}
.route-panel {
  width: 100%;
  height: 200px; /* 可以根据需要调整高度 */
  margin-top: 20px;
  border: 1px solid #eee;
  text-align: left;
  overflow-y: auto;
}
</style>