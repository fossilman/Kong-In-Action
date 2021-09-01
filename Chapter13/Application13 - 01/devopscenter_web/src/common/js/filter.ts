import Vue from 'vue';
import moment from "moment";
// 格式化时间
Vue.filter("formatDate", (date: string, types: string): string => {
  let format: string = 'YYYY-MM-DD HH:mm:ss';
  if (types) {
      format = types;
  }
  return moment(date).format(format);
});
