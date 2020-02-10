import React, {Component} from 'react';

import {
  FlatList,
  StyleSheet,
  Text,
  TouchableHighlight,
  View,
  Image,
  Platform, // 判断当前运行的系统
  Navigator,
} from 'react-native';
export default class TextCell extends Component {
  render() {
    let {movie} = this.props;
    return (
        <TouchableHighlight>
          <Text style={styles.item}>{movie.title}</Text>
        </TouchableHighlight>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 22,
    backgroundColor:'#ffffff'
  },
  item: {
    padding: 10,
    fontSize: 18,
    height: 44,
  },
});
