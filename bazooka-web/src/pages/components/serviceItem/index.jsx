import React, { Fragment } from 'react';
import { Card, Dropdown, Menu, Icon, Tag } from "antd";
import styles from "../../index.less";
import stylesService from './index.less';
import { COLOR_SHOW, APP_STATUS } from '@/common/constant';

class ServiceItem extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      envDate: [{}, {}, {}, {}],
      currentId: ''
    }
  }
  static defaultProps = {
    toTop: () => { },
    onItemClick: () => { }
  };
  //置顶
  onTop = () => {
    const { toTop } = this.props;
    toTop();
  }
  onArrowClick = (type) => {
    const { item } = this.props;
    let ele = document.getElementById(item.id);
    let positionLeft = ele.style.left || '0';
    let list = item.appEnvInfoVoList && item.appEnvInfoVoList.length > 0 ? item.appEnvInfoVoList : [];
    if (!list.length || list.length <= 3) {
      return
    }
    let moveNumber = list.length%3;
    let leftArrow = document.getElementById(`left${item.id}`), rightArrow = document.getElementById(`right${item.id}`);
    switch (type) {
      case 'left': {
        let left = Number(positionLeft.split('%')[0]);
        if (left === 0) {
          return
        }
        ele.style.left = `${left + 33.5}%`;
        rightArrow.style.color = '#1890ff';
        if(left + 33.5 === 0) {
          leftArrow.style.color = '#bbb';
        }
      } break;
      case 'right': {
        let left = Math.abs(Number(positionLeft.split('%')[0]));
        if (left === moveNumber * 33.5) {
          rightArrow.style.color = '#bbb';
          return
        }
        ele.style.left = `-${left + 33.5}%`;
        leftArrow.style.color = '#1890ff';
        if(left + 33.5 === moveNumber * 33.5) {
          rightArrow.style.color = '#bbb';
        }
      } break;
    }

  }
  renderMoreOpera(item) {
    return (
      <Menu>
        <Menu.Item onClick={() => { this.onTop() }}>
          <span>{item.orderId > 0 ? '取消置顶' : '置顶'}</span>
        </Menu.Item>
      </Menu>
    )
  }
  render() {
    const { item, onItemClick } = this.props;
    const { envDate } = this.state;
    return (
      <Card className={`${styles.marginB} ${styles.listItem}`} hoverable>
        <div>
          <div className={`${styles.flex} ${stylesService.itemWarp}`}>
            <p className={stylesService.listItemProject} style={{ backgroundColor: COLOR_SHOW[(item.projectId) % 5] }}>{item.projectName}</p>
            <div onClick={() => { this.onTop() }} title={item.orderId > 0 ? '取消置顶' : '置顶'}>
              <Icon type="pushpin" theme={item.orderId > 0 ? "filled" : "outlined"} style={{ fontSize: 18 }} />
            </div>
          </div>
          <div onClick={() => onItemClick()}>
            <div className={stylesService.listItemAppName}>{item.appName}</div>
            <div className={stylesService.itemDesc}>
              <span className={`${stylesService.itemDescSpan} ${styles.textC}`}>{item.description}</span>
            </div>
          </div>
          <div className={stylesService.listItemEnvBox}>
            <div className={stylesService.listItemEnv} id={item.id}>
              {
                item.appEnvInfoVoList && item.appEnvInfoVoList.map((vo, i) => (
                  <div key={i} className={stylesService.envItem}>
                    <div className={stylesService.envItemNameWrap}>
                      <div className={stylesService.envItemName} title={vo.envName}>{vo.envName}</div>
                    </div>
                    <p className={`${styles[APP_STATUS[vo.status].color]} ${stylesService.textStatus}`}>
                      {APP_STATUS[vo.status].text}
                    </p>

                  </div>
                ))
              }
            </div>
            {
              item.appEnvInfoVoList &&
              <Fragment>
                <div className={stylesService.leftArrow} onClick={() => { this.onArrowClick('left') }}>
                  <Icon type="left" style={{ color: "#bbb" }} id={`left${item.id}`} />
                </div>
                <div className={stylesService.rightArrow}>
                  <Icon type="right" style={item.appEnvInfoVoList.length > 3 ? { color: "#1890ff" } : { color: "#bbb" }} onClick={() => { this.onArrowClick('right') }} id={`right${item.id}`} />
                </div>
              </Fragment>
            }
          </div>
        </div>
        {/* <div onClick={() => onItemClick()}>
          <div className={styles.flexCenter}>
            <div className={styles.itemFirst} style={{ backgroundColor: COLOR_SHOW[(item.id) % 5] }}>{item.appName && item.appName.charAt(0).toUpperCase()}</div>
            <div className={styles.appName}>{item.projectName} / {item.appName}</div>
            {
              item.orderId > 0 &&
              <div className={styles.marginL10}>
                <Tag color="blue">置顶</Tag>
              </div>
            }

          </div>
          <div className={styles.itemDesc}>
            <span className={styles.itemDescSpan}>{item.description}</span>
          </div>
        </div>
        <div className={styles.moreOprea}>
          <Dropdown overlay={this.renderMoreOpera(item)} placement="bottomCenter">
            <Icon type="ellipsis" style={{ fontSize: 25 }} />
          </Dropdown>
        </div> */}
      </Card>
    )
  }

}
export default ServiceItem;