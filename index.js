/** 总入口 */
import {AppRegistry} from 'react-native';
// 导入外部类
import NewsListPage from './component/NewsListPage.js';
import MiniDetailPage from './component/MiniDetailPage.js';
import {name as appName} from './app.json';

// 注意，这里用引号括起来的'HelloWorldApp'必须和你init创建的项目名一致
// 以XXXApp组件为根组件渲染这个项目
AppRegistry.registerComponent(appName, () => NewsListPage);
AppRegistry.registerComponent('mini', () => MiniDetailPage);
