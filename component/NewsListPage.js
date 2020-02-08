import React, {Component} from 'react';
import {
  FlatList,
  StyleSheet,
  Text,
  View,
  Image,
  Platform, // 判断当前运行的系统
  Navigator,
} from 'react-native';
import TextCell from './TextCell.js';
import {get} from 'react-native/Libraries/TurboModule/TurboModuleRegistry';

// Component是一切界面组件的根类,实现界面组件必须要像继承此类
class NewsListPage extends Component {
  constructor() {
    super();
    this.state = {
      movieList: [], // 电影列表的数据源
      loaded: false, // 用来控制loading视图的显示，当数据加载完成，loading视图不再显示
    };
  }
  // 在render()之后立即执行；
  componentDidMount(): void {
    this._initData(10);
  }

  render() {
    for (let i = 0; i < this.state.movieList.length - 1; i++) {
      console.debug('+++++++++++++++1');
      console.debug(this.state.movieList[i].id);
    }
    return (
      <FlatList
        data={this.state.movieList}
        renderItem={this._renderItem}
        // 用于生成一个唯一的key作为列表中每一项的身份标识
        keyExtractor={item => item.id}
      />
    );
  }
  /** 创建item的视图*/
  _renderItem = item => {
    return (
      <TextCell
        movie={item.item}
        onPress={() => {
          console.log('点击了电影----' + item.item.title);
        }}
      />
    );
  };
  /** 获取数据 */
  _initData(num) {
    let that = this;
    fetch('http://111.229.116.167:8088/news/queryByNum?num=' + num)
      .then(response => response.json())
      .then(responseJson => {
        for (let idx in responseJson.data) {
          let newsItem = responseJson.data[idx];
          newsItem.id = idx;
        }
        that.setState({
          movieList: responseJson.data,
          loaded: true,
        });
        return responseJson.data;
      })
      .catch(error => {
        console.error(error);
      })
      .done();
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 22,
  },
  item: {
    padding: 10,
    fontSize: 18,
    height: 44,
  },
});

// 输出组件类
export default NewsListPage;
// module.exports = NewsListApp;
